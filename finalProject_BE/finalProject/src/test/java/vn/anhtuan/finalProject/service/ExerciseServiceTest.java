package vn.anhtuan.finalProject.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.anhtuan.finalProject.dto.request.ExerciseRequest;
import vn.anhtuan.finalProject.entity.ContentType;
import vn.anhtuan.finalProject.entity.Exercise;
import vn.anhtuan.finalProject.entity.Lesson;
import vn.anhtuan.finalProject.exception.ResourceNotFoundException;
import vn.anhtuan.finalProject.repository.ExerciseRepository;
import vn.anhtuan.finalProject.repository.LessonRepository;
import vn.anhtuan.finalProject.repository.ExerciseSubmissionRepository;
import vn.anhtuan.finalProject.entity.ExerciseSubmission;
import vn.anhtuan.finalProject.dto.response.ExerciseSubmissionResponse;
import vn.anhtuan.finalProject.dto.request.SaveSubmissionRequest;
import vn.anhtuan.finalProject.entity.User;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExerciseServiceTest {

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private ExerciseSubmissionRepository submissionRepository;

    @Mock
    private IUserService userService;

    @InjectMocks
    private ExerciseService exerciseService;

    private Lesson lesson;
    private Exercise exercise;

    @BeforeEach
    void setUp() {
        lesson = new Lesson();
        lesson.setId(1L);
        lesson.setTitleVi("Bài 1");

        exercise = new Exercise();
        exercise.setId(1L);
        exercise.setQuestionType(ContentType.TEXT);
        exercise.setQuestionValueVi("Câu hỏi 1");
        exercise.setLesson(lesson);
        exercise.setOrderNo(1);
    }

    @Test
    void testCreateExercise_Success() {
        ExerciseRequest request = new ExerciseRequest(1L, ContentType.TEXT, "Câu hỏi 1", "Question 1", 1, new ArrayList<>());
        when(lessonRepository.findById(1L)).thenReturn(Optional.of(lesson));
        when(exerciseRepository.save(any(Exercise.class))).thenReturn(exercise);

        Exercise result = exerciseService.createExercise(request);
        assertNotNull(result);
        assertEquals("Câu hỏi 1", result.getQuestionValueVi());
        verify(exerciseRepository).save(any(Exercise.class));
    }

    @Test
    void testGetExerciseById_Success() {
        when(exerciseRepository.findById(1L)).thenReturn(Optional.of(exercise));
        Exercise result = exerciseService.getExerciseById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetExerciseById_NotFound() {
        when(exerciseRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> exerciseService.getExerciseById(1L));
    }

    @Test
    void testDeleteExercise_Success() {
        when(exerciseRepository.existsById(1L)).thenReturn(true);
        exerciseService.deleteExercise(1L);
        verify(exerciseRepository).deleteById(1L);
    }

    @Test
    void testSaveSubmission_Success() {
        User user = new User();
        user.setId(1L);
        SaveSubmissionRequest request = new SaveSubmissionRequest("url", "reason");
        
        when(userService.getCurrentUser()).thenReturn(user);
        when(exerciseRepository.findById(1L)).thenReturn(Optional.of(exercise));
        when(submissionRepository.save(any(ExerciseSubmission.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ExerciseSubmissionResponse result = exerciseService.saveSubmission(1L, request);

        assertNotNull(result);
        assertEquals("url", result.getImageUrl());
        assertTrue(result.getValid()); // Verified use of getValid()
        verify(submissionRepository).save(any(ExerciseSubmission.class));
    }
}
