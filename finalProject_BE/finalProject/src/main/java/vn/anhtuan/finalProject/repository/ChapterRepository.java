package vn.anhtuan.finalProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.anhtuan.finalProject.entity.Chapter;
import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    List<Chapter> findBySubjectIdOrderByOrderNoAsc(Integer subjectId);

    @Query("SELECT COUNT(c.id) FROM Chapter c WHERE c.subject.code = :subjectCode AND c.subject.grade.id = :gradeId")
    int countBySubjectCodeAndGradeId(@Param("subjectCode") String subjectCode, @Param("gradeId") Integer gradeId);
}