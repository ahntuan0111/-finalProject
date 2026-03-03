package vn.anhtuan.finalProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.anhtuan.finalProject.entity.Progress;

import java.util.List;
import java.util.Optional;

public interface ProgressRepository extends JpaRepository<Progress, Long> {
    Optional<Progress> findByUserIdAndSubjectId(Long userId, Integer subjectId);

    // THÊM MỚI: Lấy toàn bộ tiến trình các môn của 1 user theo khối lớp
    List<Progress> findByUserIdAndGradeId(Long userId, Integer gradeId);
}
