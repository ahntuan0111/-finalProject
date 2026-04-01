package vn.anhtuan.finalProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.anhtuan.finalProject.entity.Document;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    // Tìm chính xác theo subjectCode chuẩn (vd: "toan") và grade (vd: 6)
    // Sau khi migration dữ liệu đã chuẩn hóa, query này là đủ
    List<Document> findBySubjectCodeAndGrade(String subjectCode, Integer grade);

    // Tìm kiếm theo từ khóa trong tiêu đề (hỗ trợ đa ngôn ngữ)
    @Query("SELECT d FROM Document d WHERE d.grade = :gradeId " +
           "AND (LOWER(d.titleVi) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(d.titleEn) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Document> searchByKeywordAndGrade(
            @Param("keyword") String keyword,
            @Param("gradeId") Integer gradeId);

    // Tìm tất cả document theo grade (dùng cho màn hình chọn lớp)
    List<Document> findByGradeOrderBySubjectCodeAsc(Integer grade);
}