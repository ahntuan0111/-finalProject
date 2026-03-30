package vn.anhtuan.finalProject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import vn.anhtuan.finalProject.dto.request.LessonCompletionRequest;
import vn.anhtuan.finalProject.dto.response.ProgressResponse;
import vn.anhtuan.finalProject.dto.response.SubjectOverviewResponse;
import vn.anhtuan.finalProject.entity.CompletionType;
import vn.anhtuan.finalProject.service.ILessonProgressService;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LessonProgressControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private ILessonProgressService lessonProgressService;
    @Autowired private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void testMarkLessonAsCompleted_Success() throws Exception {
        LessonCompletionRequest request = new LessonCompletionRequest(1L, CompletionType.THEORY);
        doNothing().when(lessonProgressService).markAsCompleted(any());

        mockMvc.perform(post("/api/progress/mark-complete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Cập nhật tiến trình thành công!"));
    }

    @Test
    @WithMockUser
    void testGetMyProgress_Success() throws Exception {
        ProgressResponse response = new ProgressResponse(1, "SUB", 7, 5, 10, 50.0);
        when(lessonProgressService.getMyProgress(anyInt())).thenReturn(Collections.singletonList(response));

        mockMvc.perform(get("/api/progress/my-progress")
                .param("grade", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].subjectCode").value("SUB"))
                .andExpect(jsonPath("$.data[0].progressPercent").value(50.0));
    }

    @Test
    @WithMockUser
    void testGetSubjectOverview_Success() throws Exception {
        SubjectOverviewResponse response = new SubjectOverviewResponse(75.5, 20L);
        when(lessonProgressService.getSubjectOverview(anyString(), anyInt())).thenReturn(response);

        mockMvc.perform(get("/api/progress/overview")
                .param("subjectCode", "MATH")
                .param("grade", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.progressPercent").value(75.5))
                .andExpect(jsonPath("$.data.nextLessonId").value(20));
    }
}
