package vn.anhtuan.finalProject.controller;

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
import vn.anhtuan.finalProject.entity.Chapter;
import vn.anhtuan.finalProject.entity.Lesson;
import vn.anhtuan.finalProject.repository.ChapterRepository;
import vn.anhtuan.finalProject.repository.LessonRepository;
import vn.anhtuan.finalProject.repository.SubjectRepository;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TheoryUserControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private ChapterRepository chapterRepository;
    @MockBean private LessonRepository lessonRepository;
    @MockBean private SubjectRepository subjectRepository;

    private Lesson testLesson;

    @BeforeEach
    void setUp() {
        testLesson = new Lesson();
        testLesson.setId(1L);
        testLesson.setTitleVi("Bài 1");
        testLesson.setTitleEn("Lesson 1");
        
        Chapter chapter = new Chapter();
        chapter.setId(10L);
        testLesson.setChapter(chapter);
    }

    @Test
    @WithMockUser
    void testGetLessonDetail_Vi_Success() throws Exception {
        when(lessonRepository.findById(1L)).thenReturn(Optional.of(testLesson));

        mockMvc.perform(get("/api/student/lessons/1")
                .header("Accept-Language", "vi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Bài 1"));
    }

    @Test
    @WithMockUser
    void testGetLessonDetail_En_Success() throws Exception {
        when(lessonRepository.findById(1L)).thenReturn(Optional.of(testLesson));

        mockMvc.perform(get("/api/student/lessons/1")
                .header("Accept-Language", "en"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Lesson 1"));
    }
}
