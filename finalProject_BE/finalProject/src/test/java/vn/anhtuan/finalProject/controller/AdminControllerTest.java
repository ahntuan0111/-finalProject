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
import vn.anhtuan.finalProject.dto.request.ChapterRequest;
import vn.anhtuan.finalProject.dto.request.LessonRequest;
import vn.anhtuan.finalProject.dto.response.UserResponse;
import vn.anhtuan.finalProject.entity.Chapter;
import vn.anhtuan.finalProject.entity.Lesson;
import vn.anhtuan.finalProject.entity.Subject;
import vn.anhtuan.finalProject.service.IUserService;
import vn.anhtuan.finalProject.service.impl.AdminContentServiceImpl;

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
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminContentServiceImpl adminContentService;

    @MockBean
    private IUserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private Subject subject;
    private Chapter chapter;
    private Lesson lesson;

    @BeforeEach
    void setUp() {
        subject = new Subject();
        subject.setId(1);
        subject.setNameVi("Toán học");

        chapter = new Chapter();
        chapter.setId(1L);
        chapter.setTitleVi("Chương 1");
        chapter.setSubject(subject);

        lesson = new Lesson();
        lesson.setId(1L);
        lesson.setTitleVi("Bài 1");
        lesson.setChapter(chapter);
        lesson.setContents(new ArrayList<>());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllUsers() throws Exception {
        UserResponse userResponse = new UserResponse();
        userResponse.setUsername("admin");
        when(userService.getAllUsers()).thenReturn(Collections.singletonList(userResponse));

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].username").value("admin"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateChapter() throws Exception {
        ChapterRequest request = new ChapterRequest(1, "Chapter 1", "Chương 1", 1);
        when(adminContentService.createChapter(any(ChapterRequest.class))).thenReturn(chapter);

        mockMvc.perform(post("/api/admin/chapters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.titleVi").value("Chương 1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateChapter() throws Exception {
        ChapterRequest request = new ChapterRequest(1, "Updated", "Cập nhật", 1);
        when(adminContentService.updateChapter(anyLong(), any(ChapterRequest.class))).thenReturn(chapter);

        mockMvc.perform(put("/api/admin/chapters/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.titleVi").value("Chương 1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteChapter() throws Exception {
        mockMvc.perform(delete("/api/admin/chapters/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Xóa chương thành công"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateLesson() throws Exception {
        LessonRequest request = new LessonRequest(1L, new ArrayList<>(), "url", 1, "Up", "Cn");
        when(adminContentService.updateLesson(anyLong(), any(LessonRequest.class))).thenReturn(lesson);

        mockMvc.perform(put("/api/admin/lessons/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.titleVi").value("Bài 1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteLesson() throws Exception {
        mockMvc.perform(delete("/api/admin/lessons/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Xóa bài học thành công"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testAccessDeniedForNonAdmin() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isForbidden());
    }
}
