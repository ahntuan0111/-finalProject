package vn.anhtuan.finalProject.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.anhtuan.finalProject.dto.request.ChapterRequest;
import vn.anhtuan.finalProject.dto.request.LessonContentRequest;
import vn.anhtuan.finalProject.dto.request.LessonRequest;
import vn.anhtuan.finalProject.entity.*;
import vn.anhtuan.finalProject.exception.ResourceNotFoundException;
import vn.anhtuan.finalProject.repository.ChapterRepository;
import vn.anhtuan.finalProject.repository.LessonRepository;
import vn.anhtuan.finalProject.repository.SubjectRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminContentServiceImplTest {

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private ChapterRepository chapterRepository;

    @Mock
    private LessonRepository lessonRepository;

    @InjectMocks
    private AdminContentServiceImpl adminContentService;

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
    }

    @Test
    void testGetAllSubjects() {
        when(subjectRepository.findAll()).thenReturn(Collections.singletonList(subject));
        List<Subject> result = adminContentService.getAllSubjects();
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void testCreateChapter() {
        ChapterRequest request = new ChapterRequest(1, "Chapter 1", "Chương 1", 1);
        when(subjectRepository.findById(1)).thenReturn(Optional.of(subject));
        when(chapterRepository.save(any(Chapter.class))).thenReturn(chapter);

        Chapter result = adminContentService.createChapter(request);
        assertNotNull(result);
        assertEquals("Chương 1", result.getTitleVi());
        verify(chapterRepository).save(any(Chapter.class));
    }

    @Test
    void testCreateLesson() {
        LessonContentRequest contentReq = new LessonContentRequest(ContentType.TEXT, 1, "Content 1", "Nội dung 1");
        LessonRequest request = new LessonRequest(1L, Collections.singletonList(contentReq), "http://video.com", 1, "Lesson 1", "Bài 1");

        when(chapterRepository.findById(1L)).thenReturn(Optional.of(chapter));
        when(lessonRepository.save(any(Lesson.class))).thenReturn(lesson);

        Lesson result = adminContentService.createLesson(request);
        assertNotNull(result);
        assertEquals("Bài 1", result.getTitleVi());
        verify(lessonRepository).save(any(Lesson.class));
    }

    @Test
    void testUpdateChapter_NotFound() {
        ChapterRequest request = new ChapterRequest(1, "Chapter 1", "Chương 1", 1);
        when(chapterRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adminContentService.updateChapter(1L, request));
    }

    @Test
    void testDeleteLesson_Success() {
        when(lessonRepository.existsById(1L)).thenReturn(true);
        adminContentService.deleteLesson(1L);
        verify(lessonRepository).deleteById(1L);
    }
}
