package vn.anhtuan.finalProject.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.anhtuan.finalProject.dto.response.LessonResponse;
import vn.anhtuan.finalProject.entity.ContentType;
import vn.anhtuan.finalProject.entity.Lesson;
import vn.anhtuan.finalProject.repository.LessonRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LessonServiceTest {

    @Mock private LessonRepository lessonRepository;

    @InjectMocks
    private LessonService lessonService;

    private Lesson testLesson;

    @BeforeEach
    void setUp() {
        testLesson = new Lesson();
        testLesson.setId(1L);
        testLesson.setTitleVi("Bài 1 Tiếng Việt");
        testLesson.setTitleEn("Lesson 1 English");
    }

    @Test
    void testGetLessonsByChapterForMobile_Localization() {
        // Arrange
        when(lessonRepository.findByChapterIdOrderByOrderNoAsc(1L))
                .thenReturn(Collections.singletonList(testLesson));

        // Act
        List<LessonResponse> viList = lessonService.getLessonsForMobile(1L, "vi");
        List<LessonResponse> enList = lessonService.getLessonsForMobile(1L, "en");

        // Assert
        assertThat(viList.get(0).getTitle()).isEqualTo("Bài 1 Tiếng Việt");
        assertThat(enList.get(0).getTitle()).isEqualTo("Lesson 1 English");
    }

    @Test
    void testGetLessonByIdForMobile_Success() {
        // Arrange
        when(lessonRepository.findById(1L)).thenReturn(Optional.of(testLesson));

        // Act
        LessonResponse response = lessonService.getLessonByIdForMobile(1L, "vi");

        // Assert
        assertThat(response.getTitle()).isEqualTo("Bài 1 Tiếng Việt");
    }
}
