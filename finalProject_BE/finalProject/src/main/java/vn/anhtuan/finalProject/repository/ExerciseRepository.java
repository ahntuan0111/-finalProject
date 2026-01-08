package vn.anhtuan.finalProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.anhtuan.finalProject.entity.Exercise;

import java.util.List;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    // Lấy danh sách bài tập của một bài học, sắp xếp theo thứ tự
    List<Exercise> findByLessonIdOrderByOrderNoAsc(Long lessonId);
}