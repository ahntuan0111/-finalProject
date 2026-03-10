package vn.anhtuan.finalProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.anhtuan.finalProject.entity.Lesson;

import java.time.LocalDateTime;
import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findByChapterIdOrderByOrderNoAsc(Long chapterId);

    // Trong LessonRepository.java thêm dòng này để đếm tổng số bài học của 1 môn:
    int countByChapter_Subject_Id(Integer subjectId);

    // Tìm các bài học CHƯA HOÀN THÀNH của 1 user theo môn và khối, sắp xếp theo thứ tự
    @Query("SELECT l.id FROM Lesson l " +
            "WHERE l.chapter.subject.code = :subjectCode AND l.chapter.subject.grade.id = :gradeId " +
            "AND l.id NOT IN (" +
            "  SELECT lc.lesson.id FROM LessonCompletion lc " +
            "  WHERE lc.user.id = :userId AND lc.completedAt IS NOT NULL" +
            ") " +
            "ORDER BY l.chapter.orderNo ASC, l.orderNo ASC")
    List<Long> findUncompletedLessonIds(@Param("userId") Long userId, @Param("subjectCode") String subjectCode, @Param("gradeId") Integer gradeId);

    // Lấy tất cả bài học của môn đó để làm fallback (nếu đã học xong hết)
    @Query("SELECT l.id FROM Lesson l " +
            "WHERE l.chapter.subject.code = :subjectCode AND l.chapter.subject.grade.id = :gradeId " +
            "ORDER BY l.chapter.orderNo ASC, l.orderNo ASC")
    List<Long> findAllLessonIds(@Param("subjectCode") String subjectCode, @Param("gradeId") Integer gradeId);

    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    List<Lesson> findTop5ByOrderByCreatedAtDesc();

    // Query đếm số bài học theo từng Môn học (Cho biểu đồ Donut)
    // Giả định Lesson liên kết với Chapter, Chapter liên kết với Subject
    @Query("SELECT s.name, COUNT(l.id) FROM Lesson l JOIN l.chapter c JOIN c.subject s GROUP BY s.name")
    List<Object[]> countLessonsGroupedBySubject();
}