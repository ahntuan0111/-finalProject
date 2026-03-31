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
import vn.anhtuan.finalProject.dto.request.ChapterRequest;
import vn.anhtuan.finalProject.entity.Chapter;
import vn.anhtuan.finalProject.service.ChapterService;
import vn.anhtuan.finalProject.service.impl.AdminContentServiceImpl;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AdminChapterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChapterService chapterService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetChaptersBySubject() throws Exception {
        Chapter chapter = new Chapter();
        chapter.setId(1L);
        chapter.setTitleVi("Chương 1");

        when(chapterService.getChaptersBySubject(anyInt())).thenReturn(Collections.singletonList(chapter));

        mockMvc.perform(get("/api/admin/chapters/by-subject/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].titleVi").value("Chương 1"));
    }
}
