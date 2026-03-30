package vn.anhtuan.finalProject.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.anhtuan.finalProject.dto.request.LessonCompletionRequest;
import vn.anhtuan.finalProject.entity.*;
import vn.anhtuan.finalProject.repository.LessonCompletionRepository;
import vn.anhtuan.finalProject.repository.LessonRepository;
import vn.anhtuan.finalProject.repository.ProgressRepository;
import vn.anhtuan.finalProject.service.IUserService;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LessonProgressServiceTest {

    @Mock private LessonCompletionRepository lessonCompletionRepository;
    @Mock private ProgressRepository progressRepository;
    @Mock private LessonRepository lessonRepository;
    @Mock private IUserService userService;

    @InjectMocks
    private LessonProgressServiceImpl lessonProgressService;

    private User testUser;
    private Lesson testLesson;
    private Subject testSubject;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testSubject = new Subject();
        testSubject.setId(10);
        testSubject.setCode("MATH");

        Chapter chapter = new Chapter();
        chapter.setSubject(testSubject);

        testLesson = new Lesson();
        testLesson.setId(100L);
        testLesson.setChapter(chapter);
    }

    @Test
    void testMarkAsCompleted_TheoryOnly_DoesNotSetCompletedAt() {
        // Arrange
        LessonCompletionRequest request = new LessonCompletionRequest(100L, CompletionType.THEORY);
        when(userService.getCurrentUser()).thenReturn(testUser);
        when(lessonRepository.findById(100L)).thenReturn(Optional.of(testLesson));
        when(lessonCompletionRepository.findByUserIdAndLessonId(1L, 100L)).thenReturn(Optional.empty());

        // Act
        lessonProgressService.markAsCompleted(request);

        // Assert
        verify(lessonCompletionRepository).save(argThat(lc -> 
            lc.isTheoryCompleted() && !lc.isExerciseCompleted() && lc.getCompletedAt() == null
        ));
        verify(userService).recordDailyActivity("testuser");
    }

    @Test
    void testMarkAsCompleted_BothDone_SetsCompletedAt() {
        // Arrange
        LessonCompletionRequest request = new LessonCompletionRequest(100L, CompletionType.EXERCISE);
        LessonCompletion existing = new LessonCompletion();
        existing.setUser(testUser);
        existing.setLesson(testLesson);
        existing.setTheoryCompleted(true); // Đã học lý thuyết trước đó

        when(userService.getCurrentUser()).thenReturn(testUser);
        when(lessonRepository.findById(100L)).thenReturn(Optional.of(testLesson));
        when(lessonCompletionRepository.findByUserIdAndLessonId(1L, 100L)).thenReturn(Optional.of(existing));
        
        // Mocking for updateSubjectProgress
        when(lessonRepository.countByChapter_Subject_Id(anyInt())).thenReturn(10);
        when(lessonCompletionRepository.countCompletedLessonsByUserAndSubject(anyLong(), anyInt())).thenReturn(5);
        when(progressRepository.findByUserIdAndSubjectId(anyLong(), anyInt())).thenReturn(Optional.empty());

        // Act
        lessonProgressService.markAsCompleted(request);

        // Assert
        verify(lessonCompletionRepository).save(argThat(lc -> 
            lc.isTheoryCompleted() && lc.isExerciseCompleted() && lc.getCompletedAt() != null
        ));
        verify(progressRepository).save(any(Progress.class)); // triggered updateSubjectProgress
    }

    @Test
    void testGetSubjectOverview_ReturnsNextLesson() {
        // Arrange
        when(userService.getCurrentUser()).thenReturn(testUser);
        when(progressRepository.findByUserIdAndGradeId(1L, 7)).thenReturn(Collections.emptyList());
        when(lessonRepository.findUncompletedLessonIds(1L, "MATH", 7)).thenReturn(Collections.singletonList(200L));

        // Act
        var overview = lessonProgressService.getSubjectOverview("MATH", 7);

        // Assert
        assertThat(overview.getNextLessonId()).isEqualTo(200L);
    }
}
