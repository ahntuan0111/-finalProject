package vn.anhtuan.finalProject.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import vn.anhtuan.finalProject.entity.*;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class LessonCompletionRepositoryTest {

    @Autowired private LessonCompletionRepository completionRepository;
    @Autowired private LessonRepository lessonRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ChapterRepository chapterRepository;
    @Autowired private SubjectRepository subjectRepository;
    @Autowired private GradeRepository gradeRepository;

    private Long userId;
    private Integer subjectId;
    private Long lessonId;

    @BeforeEach
    void setUp() {
        // User
        User user = new User();
        user.setUsername("stud1");
        user.setEmail("stud1@test.com");
        user.setPassword("pass");
        userRepository.save(user);
        userId = user.getId();

        // Grade
        Grade grade = new Grade();
        grade.setId(201);
        grade.setCode("G201");
        grade.setNameVi("Khối 201");
        gradeRepository.save(grade);

        // Subject
        Subject subject = new Subject();
        subject.setCode("S201");
        subject.setGrade(grade);
        subject.setNameVi("Sub 201");
        subjectRepository.save(subject);
        subjectId = subject.getId();

        // Chapter
        Chapter chapter = new Chapter();
        chapter.setSubject(subject);
        chapter.setTitleVi("Ch 201");
        chapter.setOrderNo(1);
        chapterRepository.save(chapter);

        // Lesson
        Lesson lesson = new Lesson();
        lesson.setChapter(chapter);
        lesson.setTitleVi("Les 201");
        lesson.setOrderNo(1);
        lessonRepository.save(lesson);
        lessonId = lesson.getId();

        // Completion
        LessonCompletion completion = new LessonCompletion();
        completion.setUser(user);
        completion.setLesson(lesson);
        completion.setTheoryCompleted(true);
        completion.setExerciseCompleted(true);
        completion.setCompletedAt(LocalDateTime.now());
        completionRepository.save(completion);
    }

    @Test
    void testCountCompletedLessonsByUserAndSubject() {
        int count = completionRepository.countCompletedLessonsByUserAndSubject(userId, subjectId);
        assertThat(count).isEqualTo(1);
    }

    @Test
    void testFindByUserIdAndLessonId() {
        assertThat(completionRepository.findByUserIdAndLessonId(userId, lessonId)).isPresent();
    }
}
