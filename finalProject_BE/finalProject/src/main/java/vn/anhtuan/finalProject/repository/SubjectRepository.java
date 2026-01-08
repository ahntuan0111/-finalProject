package vn.anhtuan.finalProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.anhtuan.finalProject.entity.Subject;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Integer> {
    // Tìm môn học theo khối (Ví dụ: Lấy tất cả môn của khối 6)
    List<Subject> findByGradeId(Integer gradeId);

    // Tìm theo mã môn (ví dụ: 'toan')
    Subject findByCode(String code);

    // Tìm Subject dựa vào mã (vd: "toan") và khối (vd: 7)
    Optional<Subject> findByCodeAndGradeId(String code, Integer gradeId);
}