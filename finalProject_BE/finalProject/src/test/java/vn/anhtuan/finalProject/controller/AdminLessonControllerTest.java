package vn.anhtuan.finalProject.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import vn.anhtuan.finalProject.entity.Lesson;
import vn.anhtuan.finalProject.service.LessonService;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AdminLessonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LessonService lessonService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetLessonsByChapter() throws Exception {
        Lesson lesson = new Lesson();
        lesson.setId(1L);
        lesson.setTitleVi("Bài 1");

        when(lessonService.getLessonsByChapter(anyLong())).thenReturn(Collections.singletonList(lesson));

        mockMvc.perform(get("/api/admin/lessons/by-chapter/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].titleVi").value("Bài 1"));
    }
}
