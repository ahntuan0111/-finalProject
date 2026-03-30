package vn.anhtuan.finalProject.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import vn.anhtuan.finalProject.entity.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class QuizRepositoryTest {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private ChapterRepository chapterRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private QuizTypeRepository quizTypeRepository;

    private Integer gradeId = 10;
    private String subjectCode = "TOAN";

    @BeforeEach
    void setUp() {
        // Tạo Grade
        Grade grade = new Grade();
        grade.setId(gradeId);
        grade.setNameVi("Khối 10");
        grade.setCode("K10");
        gradeRepository.save(grade);

        // Tạo Subject
        Subject subject = new Subject();
        subject.setCode(subjectCode);
        subject.setNameVi("Toán học");
        subject.setGrade(grade);
        subjectRepository.save(subject);

        // Tạo Chapter
        Chapter chapter = new Chapter();
        chapter.setTitleVi("Chương 1: Mệnh đề");
        chapter.setOrderNo(1);
        chapter.setSubject(subject);
        chapterRepository.save(chapter);

        // Tạo QuizType
        QuizType type = new QuizType();
        type.setId(1);
        type.setNameVi("Trắc nghiệm");
        type.setDurationMinutes(15);
        type.setQuestionCount(10);
        quizTypeRepository.save(type);

        // Tạo Quiz
        Quiz quiz = new Quiz();
        quiz.setTitleVi("Kiểm tra chương 1");
        quiz.setCode("QUIZ001");
        quiz.setChapter(chapter);
        quiz.setQuizType(type);
        quizRepository.save(quiz);
    }

    @Test
    void testFindQuizzesBySubjectAndGrade_Found() {
        List<Quiz> quizzes = quizRepository.findQuizzesBySubjectAndGrade(subjectCode, gradeId);
        assertThat(quizzes).isNotEmpty();
        assertThat(quizzes.get(0).getTitleVi()).isEqualTo("Kiểm tra chương 1");
    }

    @Test
    void testSearchByKeywordAndGrade_Found() {
        List<Quiz> result = quizRepository.searchByKeywordAndGrade("Kiểm tra", gradeId);
        assertThat(result).isNotEmpty();
    }
}
