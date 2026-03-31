package vn.anhtuan.finalProject.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import vn.anhtuan.finalProject.repository.DocumentRepository;
import vn.anhtuan.finalProject.repository.LessonRepository;
import vn.anhtuan.finalProject.repository.QuizRepository;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LessonRepository lessonRepository;

    @MockBean
    private QuizRepository quizRepository;

    @MockBean
    private DocumentRepository documentRepository;

    @Test
    @WithMockUser(username = "testuser")
    void testSearchAll_Success() throws Exception {
        when(lessonRepository.searchByKeywordAndGrade(anyString(), anyInt())).thenReturn(new ArrayList<>());
        when(quizRepository.searchByKeywordAndGrade(anyString(), anyInt())).thenReturn(new ArrayList<>());
        when(documentRepository.searchByKeywordAndGrade(anyString(), anyInt())).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/student/search")
                        .param("keyword", "math")
                        .param("grade", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Search successful"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testSearchAll_NoParams() throws Exception {
        mockMvc.perform(get("/api/student/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("No keyword or grade provided"));
    }
}
