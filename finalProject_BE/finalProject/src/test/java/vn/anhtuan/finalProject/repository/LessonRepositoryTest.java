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
class LessonRepositoryTest {

    @Autowired private LessonRepository lessonRepository;
    @Autowired private ChapterRepository chapterRepository;
    @Autowired private SubjectRepository subjectRepository;
    @Autowired private GradeRepository gradeRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private LessonCompletionRepository completionRepository;

    private Long chapterId;
    private Integer gradeId = 101;
    private String subjectCode = "TEST_SUB";

    @BeforeEach
    void setUp() {
        Grade grade = new Grade();
        grade.setId(gradeId);
        grade.setCode("G101");
        grade.setNameVi("Khối 101");
        gradeRepository.save(grade);

        Subject subject = new Subject();
        subject.setCode(subjectCode);
        subject.setNameVi("Môn học Test");
        subject.setGrade(grade);
        subjectRepository.save(subject);

        Chapter chapter = new Chapter();
        chapter.setTitleVi("Chương 1");
        chapter.setOrderNo(1);
        chapter.setSubject(subject);
        chapterRepository.save(chapter);
        chapterId = chapter.getId();

        Lesson l1 = new Lesson();
        l1.setTitleVi("Bài 1");
        l1.setOrderNo(1);
        l1.setChapter(chapter);
        lessonRepository.save(l1);

        Lesson l2 = new Lesson();
        l2.setTitleVi("Bài 2");
        l2.setOrderNo(2);
        l2.setChapter(chapter);
        lessonRepository.save(l2);
    }

    @Test
    void testFindByChapterIdOrderByOrderNoAsc() {
        List<Lesson> lessons = lessonRepository.findByChapterIdOrderByOrderNoAsc(chapterId);
        assertThat(lessons).hasSize(2);
        assertThat(lessons.get(0).getOrderNo()).isEqualTo(1);
    }

    @Test
    void testSearchByKeywordAndGrade() {
        List<Lesson> results = lessonRepository.searchByKeywordAndGrade("Bài 1", gradeId);
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitleVi()).isEqualTo("Bài 1");
    }
}
