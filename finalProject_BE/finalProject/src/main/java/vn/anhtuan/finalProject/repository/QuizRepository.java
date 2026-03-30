package vn.anhtuan.finalProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.anhtuan.finalProject.entity.Quiz;
import vn.anhtuan.finalProject.entity.QuizType;

import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Integer> {
    boolean existsByCode(String code);

    // Sử dụng UPPER() để không phân biệt hoa thường.
    // Giả định Grade được liên kết thông qua Subject (q.chapter.subject.grade.id)
    @Query("SELECT q FROM Quiz q WHERE UPPER(q.chapter.subject.code) = UPPER(:subjectCode) AND q.chapter.subject.grade.id = :grade")
    List<Quiz> findQuizzesBySubjectAndGrade(@Param("subjectCode") String subjectCode, @Param("grade") Integer grade);

    @Query("SELECT q FROM Quiz q JOIN q.chapter c JOIN c.subject s WHERE s.grade.id = :gradeId " +
           "AND (LOWER(q.titleVi) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(q.titleEn) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Quiz> searchByKeywordAndGrade(@Param("keyword") String keyword, @Param("gradeId") Integer gradeId);
}