package vn.anhtuan.finalProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.anhtuan.finalProject.entity.Document;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    // Tìm tài liệu theo Môn và Khối lớp (cho App Android)
    List<Document> findBySubjectCodeAndGrade(String subjectCode, Integer grade);

    // Tìm theo loại đề thi (nếu cần)
    List<Document> findBySubjectCodeAndGradeAndExamType(String subjectCode, Integer grade, String examType);
}
