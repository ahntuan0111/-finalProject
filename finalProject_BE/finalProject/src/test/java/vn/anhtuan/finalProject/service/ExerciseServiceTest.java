package vn.anhtuan.finalProject.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.anhtuan.finalProject.dto.response.ExerciseResponse;
import vn.anhtuan.finalProject.entity.*;
import vn.anhtuan.finalProject.repository.ExerciseRepository;
import vn.anhtuan.finalProject.repository.ExerciseSubmissionRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExerciseServiceTest {

    @Mock private ExerciseRepository exerciseRepository;
    @Mock private GeminiValidationService geminiValidationService;
    @Mock private IUserService userService;
    @Mock private ExerciseSubmissionRepository submissionRepository;

    @InjectMocks
    private ExerciseService exerciseService;

    private Exercise testExercise;

    @BeforeEach
    void setUp() {
        testExercise = new Exercise();
        testExercise.setId(1L);
        testExercise.setQuestionType(ContentType.TEXT);
        testExercise.setQuestionValueVi("Câu hỏi tiếng Việt");
        testExercise.setQuestionValueEn("English Question");
    }

    @Test
    void testGetExercisesByLessonForMobile_Localization() {
        // Arrange
        when(exerciseRepository.findByLessonIdOrderByOrderNoAsc(1L))
                .thenReturn(Collections.singletonList(testExercise));

        // Act
        List<ExerciseResponse> viList = exerciseService.getExercisesByLessonForMobile(1L, "vi");
        List<ExerciseResponse> enList = exerciseService.getExercisesByLessonForMobile(1L, "en");

        // Assert
        assertThat(viList.get(0).getQuestionValue()).isEqualTo("Câu hỏi tiếng Việt");
        assertThat(enList.get(0).getQuestionValue()).isEqualTo("English Question");
    }

    @Test
    void testValidateImage_CallsGemini() {
        // Arrange
        when(exerciseRepository.findById(1L)).thenReturn(Optional.of(testExercise));
        GeminiValidationService.GeminiValidationResult mockResult = new GeminiValidationService.GeminiValidationResult();
        mockResult.isValid = true;
        mockResult.aiReason = "OK";
        
        when(geminiValidationService.validateHandwriting(anyString(), anyString(), anyString()))
                .thenReturn(mockResult);

        // Act
        GeminiValidationService.GeminiValidationResult result = 
                exerciseService.validateImage(1L, "base64_data", "vi");

        // Assert
        assertThat(result.isValid).isTrue();
        verify(geminiValidationService, times(1)).validateHandwriting(eq("base64_data"), eq("Câu hỏi tiếng Việt"), eq("vi"));
    }

    @Test
    void testSaveSubmission_Success() {
        // Arrange
        User mockUser = new User();
        mockUser.setId(10L);
        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(exerciseRepository.findById(1L)).thenReturn(Optional.of(testExercise));

        vn.anhtuan.finalProject.dto.request.SaveSubmissionRequest request = new vn.anhtuan.finalProject.dto.request.SaveSubmissionRequest();
        request.setFirebaseImageUrl("http://firebase.com/img.jpg");
        request.setAiReason("Good job");

        // Act
        exerciseService.saveSubmission(1L, request);

        // Assert
        verify(submissionRepository, times(1)).save(any(ExerciseSubmission.class));
    }
}
