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
class ExerciseRepositoryTest {

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private ChapterRepository chapterRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizTypeRepository quizTypeRepository;

    private Long lessonId;

    @BeforeEach
    void setUp() {
        // Tạo Grade
        Grade grade = new Grade();
        grade.setId(10);
        grade.setNameVi("Khối 10");
        grade.setCode("K10");
        gradeRepository.save(grade);

        // Tạo Subject
        Subject subject = new Subject();
        subject.setCode("MATH");
        subject.setNameVi("Toán học");
        subject.setGrade(grade);
        subjectRepository.save(subject);

        // Tạo Chapter
        Chapter chapter = new Chapter();
        chapter.setTitleVi("Chương 1");
        chapter.setOrderNo(1);
        chapter.setSubject(subject);
        chapterRepository.save(chapter);

        // Tạo Lesson
        Lesson lesson = new Lesson();
        lesson.setTitleVi("Bài 1: Làm quen");
        lesson.setChapter(chapter);
        lesson.setOrderNo(1);
        lessonRepository.save(lesson);
        lessonId = lesson.getId();

        // Tạo Exercise 1
        Exercise ex1 = new Exercise();
        ex1.setQuestionValueVi("Câu 1");
        ex1.setOrderNo(2);
        ex1.setLesson(lesson);
        ex1.setQuestionType(ContentType.TEXT);
        exerciseRepository.save(ex1);

        // Tạo Exercise 2
        Exercise ex2 = new Exercise();
        ex2.setQuestionValueVi("Câu 2");
        ex2.setOrderNo(1); // Ưu tiên hàng đầu
        ex2.setLesson(lesson);
        ex2.setQuestionType(ContentType.TEXT);
        exerciseRepository.save(ex2);
    }

    @Test
    void testFindByLessonIdOrderByOrderNoAsc() {
        List<Exercise> exercises = exerciseRepository.findByLessonIdOrderByOrderNoAsc(lessonId);
        assertThat(exercises).hasSize(2);
        assertThat(exercises.get(0).getOrderNo()).isEqualTo(1); // Sắp xếp theo orderNo
        assertThat(exercises.get(1).getOrderNo()).isEqualTo(2);
    }
}
