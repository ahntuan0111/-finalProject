package vn.anhtuan.finalProject.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import vn.anhtuan.finalProject.entity.Grade;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class GradeRepositoryTest {

    @Autowired
    private GradeRepository gradeRepository;

    @Test
    void testSaveAndFindById() {
        Grade grade = new Grade();
        grade.setId(6);
        grade.setCode("K6");
        grade.setNameVi("Khối 6");
        Grade saved = gradeRepository.save(grade);

        Optional<Grade> found = gradeRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getNameVi()).isEqualTo("Khối 6");
    }

    @Test
    void testDelete() {
        Grade grade = new Grade();
        grade.setId(99);
        grade.setCode("KTEST");
        grade.setNameVi("Khối Test");
        Grade saved = gradeRepository.save(grade);

        gradeRepository.deleteById(saved.getId());
        Optional<Grade> found = gradeRepository.findById(saved.getId());
        assertThat(found).isNotPresent();
    }
}
