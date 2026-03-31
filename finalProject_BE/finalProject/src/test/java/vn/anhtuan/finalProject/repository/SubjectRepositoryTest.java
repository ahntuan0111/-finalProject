package vn.anhtuan.finalProject.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import vn.anhtuan.finalProject.entity.Grade;
import vn.anhtuan.finalProject.entity.Subject;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class SubjectRepositoryTest {

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @Test
    void testFindByCodeAndGradeId_Success() {
        Grade grade = new Grade();
        grade.setId(6);
        grade.setCode("K6");
        grade.setNameVi("Lớp 6");
        grade = gradeRepository.save(grade);

        Subject subject = new Subject();
        subject.setCode("TOAN");
        subject.setNameVi("Toán học");
        subject.setGrade(grade);
        subjectRepository.save(subject);

        Optional<Subject> found = subjectRepository.findByCodeAndGradeId("TOAN", grade.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getCode()).isEqualTo("TOAN");
    }

    @Test
    void testFindByCodeAndGradeId_NotFound() {
        Optional<Subject> found = subjectRepository.findByCodeAndGradeId("NON_EXISTENT", 99);
        assertThat(found).isNotPresent();
    }
}
