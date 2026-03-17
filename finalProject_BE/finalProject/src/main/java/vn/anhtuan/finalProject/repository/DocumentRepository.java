package vn.anhtuan.finalProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.anhtuan.finalProject.entity.Document;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    // Hàm này giữ nguyên vì subjectCode và grade không đổi tên
    List<Document> findBySubjectCodeAndGrade(String subjectCode, Integer grade);

    // Nếu trước đó bạn có hàm tìm kiếm theo loại bài thi, hãy dùng tên mới:
    // List<Document> findByExamTypeVi(String examTypeVi);
}