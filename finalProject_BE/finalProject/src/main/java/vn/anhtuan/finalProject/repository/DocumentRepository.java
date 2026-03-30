package vn.anhtuan.finalProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.anhtuan.finalProject.entity.Document;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    // Tim chinh xac theo subjectCode va grade
    List<Document> findBySubjectCodeAndGrade(String subjectCode, Integer grade);

    // Tim linh hoat: Chap nhan ca code ngan ("toan") lan ten day du ("Toan") - case insensitive
    @Query("SELECT d FROM Document d WHERE d.grade = :grade " +
           "AND (LOWER(d.subjectCode) = LOWER(:subjectCode) " +
           "OR LOWER(d.subjectCode) LIKE LOWER(CONCAT('%', :subjectCode, '%')) " +
           "OR LOWER(:subjectCode) LIKE LOWER(CONCAT('%', d.subjectCode, '%')))")
    List<Document> findBySubjectCodeFlexibleAndGrade(
            @Param("subjectCode") String subjectCode,
            @Param("grade") Integer grade);

    @Query("SELECT d FROM Document d WHERE d.grade = :gradeId " +
           "AND (LOWER(d.titleVi) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(d.titleEn) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Document> searchByKeywordAndGrade(
            @Param("keyword") String keyword,
            @Param("gradeId") Integer gradeId);
}