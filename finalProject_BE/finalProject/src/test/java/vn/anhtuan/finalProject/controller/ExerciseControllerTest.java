package vn.anhtuan.finalProject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import vn.anhtuan.finalProject.dto.response.ExerciseResponse;
import vn.anhtuan.finalProject.service.ExerciseService;
import vn.anhtuan.finalProject.service.FirebaseStorageService;
import vn.anhtuan.finalProject.service.GeminiValidationService;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ExerciseControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ExerciseService exerciseService;

    @Mock
    private FirebaseStorageService firebaseStorageService;

    @InjectMocks
    private ExerciseController exerciseController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(exerciseController).build();
    }

    @Test
    void testGetExercisesByLesson_Success() throws Exception {
        // Arrange
        ExerciseResponse response = new ExerciseResponse();
        response.setId(1L);
        response.setQuestionValue("Sample Question");
        
        when(exerciseService.getExercisesByLessonForMobile(anyLong(), anyString()))
                .thenReturn(Collections.singletonList(response));

        // Act & Assert
        mockMvc.perform(get("/api/exercises/by-lesson/1")
                        .header("Accept-Language", "vi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].questionValue").value("Sample Question"));
    }

    @Test
    void testValidateImage_Valid_ReturnsOk() throws Exception {
        // Arrange
        vn.anhtuan.finalProject.dto.request.ValidationSubmissionRequest request = 
                new vn.anhtuan.finalProject.dto.request.ValidationSubmissionRequest();
        request.setBase64Image("base64_data");

        GeminiValidationService.GeminiValidationResult mockResult = new GeminiValidationService.GeminiValidationResult();
        mockResult.isValid = true;
        mockResult.aiReason = "Looks good";

        when(exerciseService.validateImage(anyLong(), anyString(), anyString()))
                .thenReturn(mockResult);

        // Act & Assert
        mockMvc.perform(post("/api/exercises/1/validate-image")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.isValid").value(true)); // GeminiValidationResult still uses isValid
    }

    @Test
    void testValidateImage_Invalid_Returns400() throws Exception {
        // Arrange
        vn.anhtuan.finalProject.dto.request.ValidationSubmissionRequest request = 
                new vn.anhtuan.finalProject.dto.request.ValidationSubmissionRequest();
        request.setBase64Image("invalid_data");

        GeminiValidationService.GeminiValidationResult mockResult = new GeminiValidationService.GeminiValidationResult();
        mockResult.isValid = false;
        mockResult.aiReason = "Not a math problem";

        when(exerciseService.validateImage(anyLong(), anyString(), anyString()))
                .thenReturn(mockResult);

        // Act & Assert
        mockMvc.perform(post("/api/exercises/1/validate-image")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Not a math problem"));
    }
}
