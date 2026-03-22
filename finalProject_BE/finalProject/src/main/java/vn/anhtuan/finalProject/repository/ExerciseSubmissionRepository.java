package vn.anhtuan.finalProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.anhtuan.finalProject.entity.ExerciseSubmission;
import java.util.List;

@Repository
public interface ExerciseSubmissionRepository extends JpaRepository<ExerciseSubmission, Long> {
    List<ExerciseSubmission> findByUserIdAndExerciseIdOrderBySubmittedAtDesc(Long userId, Long exerciseId);
}
