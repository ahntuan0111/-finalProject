package vn.anhtuan.finalProject.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.anhtuan.finalProject.dto.response.admin.AdminDashboardResponse;
import vn.anhtuan.finalProject.entity.Chapter;
import vn.anhtuan.finalProject.entity.Lesson;
import vn.anhtuan.finalProject.entity.Subject;
import vn.anhtuan.finalProject.repository.LessonRepository;
import vn.anhtuan.finalProject.repository.QuizRepository;
import vn.anhtuan.finalProject.repository.UserRepository;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminDashboardServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private LessonRepository lessonRepository;
    @Mock
    private QuizRepository quizRepository;

    @InjectMocks
    private AdminDashboardService adminDashboardService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testGetDashboardData_BasicStats_ShouldReturnCorrectValues() {
        when(userRepository.count()).thenReturn(100L);
        when(userRepository.countByLastActiveDate(any())).thenReturn(10L);
        when(lessonRepository.count()).thenReturn(50L);
        when(quizRepository.count()).thenReturn(20L);

        // Mock growth calculations
        when(userRepository.countByCreatedAtBetween(any(), any())).thenReturn(5L); // this month
        // last month = 4, this month = 5 -> +25%
        // Wait, helper logic is simple.
        
        AdminDashboardResponse response = adminDashboardService.getDashboardData("vi");

        assertThat(response.getOverview().getTotalStudents()).isEqualTo(100L);
        assertThat(response.getOverview().getOnlineUsers()).isEqualTo(10L);
        assertThat(response.getOverview().getTotalLessons()).isEqualTo(50L);
    }

    @Test
    void testGetDashboardData_RecentContents_ShouldReturnLocalizedTitles() {
        Subject subject = new Subject();
        subject.setNameVi("Toán học");
        subject.setNameEn("Math");
        
        Chapter chapter = new Chapter();
        chapter.setSubject(subject);

        Lesson lesson = new Lesson();
        lesson.setId(1L);
        lesson.setTitleVi("Bài 1 Tiếng Việt");
        lesson.setTitleEn("Lesson 1 English");
        lesson.setChapter(chapter);

        when(lessonRepository.findTop5ByOrderByCreatedAtDesc()).thenReturn(Collections.singletonList(lesson));

        // Test Vietnamese
        AdminDashboardResponse responseVi = adminDashboardService.getDashboardData("vi");
        assertThat(responseVi.getRecentContents().get(0).getTitle()).isEqualTo("Bài 1 Tiếng Việt");

        // Test English
        AdminDashboardResponse responseEn = adminDashboardService.getDashboardData("en");
        assertThat(responseEn.getRecentContents().get(0).getTitle()).isEqualTo("Lesson 1 English");
    }
}
