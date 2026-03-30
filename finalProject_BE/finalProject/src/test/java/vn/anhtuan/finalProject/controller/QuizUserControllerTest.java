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
import vn.anhtuan.finalProject.dto.request.QuizSubmitRequest;
import vn.anhtuan.finalProject.dto.response.QuizResponse;
import vn.anhtuan.finalProject.service.IUserService;
import vn.anhtuan.finalProject.service.QuizUserService;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class QuizUserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private QuizUserService quizUserService;

    @Mock
    private IUserService userService;

    @InjectMocks
    private QuizUserController quizUserController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(quizUserController).build();
    }

    @Test
    void testGetQuizzesByFilter_Success() throws Exception {
        // Arrange
        QuizResponse response = new QuizResponse();
        response.setId(1);
        response.setTitle("Math Quiz");
        
        when(quizUserService.getQuizzesForMobile(anyString(), anyInt(), anyString()))
                .thenReturn(Collections.singletonList(response));

        // Act & Assert
        mockMvc.perform(get("/api/quizzes/filter")
                        .param("subjectCode", "TOAN")
                        .param("grade", "7")
                        .header("Accept-Language", "en"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].title").value("Math Quiz"));
    }

    @Test
    void testSubmitQuiz_Success() throws Exception {
        // Arrange
        QuizSubmitRequest request = new QuizSubmitRequest();
        request.setTotalQuestions(10);
        request.setCorrectAnswers(9);

        when(userService.getCurrentUserId()).thenReturn(100L);
        doNothing().when(quizUserService).submitQuizResult(eq(1), eq(100L), any(QuizSubmitRequest.class));

        // Act & Assert
        mockMvc.perform(post("/api/quizzes/1/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Nộp bài thành công!"));
    }
}
