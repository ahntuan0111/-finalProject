package vn.anhtuan.finalProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.anhtuan.finalProject.entity.Lesson;
import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findByChapterIdOrderByOrderNoAsc(Long chapterId);

    // Trong LessonRepository.java thêm dòng này để đếm tổng số bài học của 1 môn:
    int countByChapter_Subject_Id(Integer subjectId);
}