package vn.anhtuan.finalProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.anhtuan.finalProject.entity.Subject;

import java.util.List;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Integer> {
    // Tìm môn học theo khối (Ví dụ: Lấy tất cả môn của khối 6)
    List<Subject> findByGradeId(Integer gradeId);

    // Tìm theo mã môn (ví dụ: 'toan')
    Subject findByCode(String code);
}