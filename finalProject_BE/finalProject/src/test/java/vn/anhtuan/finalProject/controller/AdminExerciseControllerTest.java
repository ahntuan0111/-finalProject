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
import vn.anhtuan.finalProject.dto.request.ExerciseRequest;
import vn.anhtuan.finalProject.entity.ContentType;
import vn.anhtuan.finalProject.entity.Exercise;
import vn.anhtuan.finalProject.entity.Lesson;
import vn.anhtuan.finalProject.service.ExerciseService;

import java.util.ArrayList;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AdminExerciseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExerciseService exerciseService;

    @Autowired
    private ObjectMapper objectMapper;

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
    @WithMockUser(roles = "ADMIN")
    void testGetExercisesByLesson() throws Exception {
        when(exerciseService.getExercisesByLesson(1L)).thenReturn(Collections.singletonList(exercise));

        mockMvc.perform(get("/api/admin/exercises/by-lesson/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].questionValueVi").value("Câu hỏi 1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetExerciseDetail() throws Exception {
        when(exerciseService.getExerciseById(1L)).thenReturn(exercise);

        mockMvc.perform(get("/api/admin/exercises/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.questionValueVi").value("Câu hỏi 1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateExercise() throws Exception {
        ExerciseRequest request = new ExerciseRequest(1L, ContentType.TEXT, "Câu hỏi 1", "Question 1", 1, new ArrayList<>());
        when(exerciseService.createExercise(any(ExerciseRequest.class))).thenReturn(exercise);

        mockMvc.perform(post("/api/admin/exercises")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.questionValueVi").value("Câu hỏi 1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateExercise() throws Exception {
        ExerciseRequest request = new ExerciseRequest(1L, ContentType.TEXT, "Câu hỏi update", "Question update", 1, new ArrayList<>());
        exercise.setQuestionValueVi("Câu hỏi update");
        when(exerciseService.updateExercise(anyLong(), any(ExerciseRequest.class))).thenReturn(exercise);

        mockMvc.perform(put("/api/admin/exercises/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.questionValueVi").value("Câu hỏi update"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteExercise() throws Exception {
        mockMvc.perform(delete("/api/admin/exercises/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Xóa thành công"));
    }
}
