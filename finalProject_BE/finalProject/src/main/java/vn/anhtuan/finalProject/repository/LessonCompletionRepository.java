package vn.anhtuan.finalProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.anhtuan.finalProject.entity.LessonCompletion;

import java.util.Optional;

public interface LessonCompletionRepository extends JpaRepository<LessonCompletion, Long> {
    Optional<LessonCompletion> findByUserIdAndLessonId(Long userId, Long lessonId);

    // Đếm số lesson mà user đã hoàn thành (completedAt IS NOT NULL) trong 1 môn học cụ thể
    @Query("SELECT COUNT(lc) FROM LessonCompletion lc WHERE lc.user.id = :userId " +
            "AND lc.lesson.chapter.subject.id = :subjectId AND lc.completedAt IS NOT NULL")
    int countCompletedLessonsByUserAndSubject(@Param("userId") Long userId, @Param("subjectId") Integer subjectId);
}
