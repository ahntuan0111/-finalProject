package vn.anhtuan.finalProject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import vn.anhtuan.finalProject.dto.request.QuizRequest;
import vn.anhtuan.finalProject.entity.Chapter;
import vn.anhtuan.finalProject.entity.Quiz;
import vn.anhtuan.finalProject.entity.QuizType;
import vn.anhtuan.finalProject.service.QuizAdminService;

import java.util.ArrayList;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AdminQuizControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QuizAdminService quizAdminService;

    @Autowired
    private ObjectMapper objectMapper;

    private Chapter chapter;
    private QuizType quizType;
    private Quiz quiz;

    @BeforeEach
    void setUp() {
        chapter = new Chapter();
        chapter.setId(1L);
        chapter.setTitleVi("Chương 1");

        quizType = new QuizType();
        quizType.setId(1);
        quizType.setNameVi("Trắc nghiệm");
        quizType.setDurationMinutes(15);

        quiz = new Quiz();
        quiz.setId(1);
        quiz.setCode("Q001");
        quiz.setTitleVi("Bài kiểm tra 1");
        quiz.setChapter(chapter);
        quiz.setQuizType(quizType);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllQuizzes() throws Exception {
        when(quizAdminService.getAllQuizzesRaw()).thenReturn(Collections.singletonList(quiz));

        mockMvc.perform(get("/api/admin/quizzes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].code").value("Q001"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetQuizForAdmin() throws Exception {
        when(quizAdminService.getQuizById(1)).thenReturn(quiz);

        mockMvc.perform(get("/api/admin/quizzes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.code").value("Q001"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateQuiz() throws Exception {
        QuizRequest request = new QuizRequest();
        request.setChapterId(1L);
        request.setQuizTypeId(1);
        request.setCode("Q001");
        request.setTitleVi("Bài kiểm tra 1");
        request.setQuestions(new ArrayList<>());

        when(quizAdminService.createQuiz(any(QuizRequest.class))).thenReturn(quiz);

        mockMvc.perform(post("/api/admin/quizzes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.code").value("Q001"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateQuiz() throws Exception {
        QuizRequest request = new QuizRequest();
        request.setCode("Q001-Updated");
        quiz.setCode("Q001-Updated");
        when(quizAdminService.updateQuiz(anyInt(), any(QuizRequest.class))).thenReturn(quiz);

        mockMvc.perform(put("/api/admin/quizzes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.code").value("Q001-Updated"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteQuiz() throws Exception {
        mockMvc.perform(delete("/api/admin/quizzes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Xóa Quiz thành công!"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetQuizTypes() throws Exception {
        when(quizAdminService.getAllQuizTypes()).thenReturn(Collections.singletonList(quizType));

        mockMvc.perform(get("/api/admin/quizzes/types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].nameVi").value("Trắc nghiệm"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetChapters() throws Exception {
        when(quizAdminService.getAllChapters()).thenReturn(Collections.singletonList(chapter));

        mockMvc.perform(get("/api/admin/quizzes/chapters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].titleVi").value("Chương 1"));
    }
}
