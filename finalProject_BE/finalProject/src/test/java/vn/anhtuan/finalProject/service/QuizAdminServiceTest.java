package vn.anhtuan.finalProject.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.anhtuan.finalProject.dto.request.QuizRequest;
import vn.anhtuan.finalProject.entity.Chapter;
import vn.anhtuan.finalProject.entity.Quiz;
import vn.anhtuan.finalProject.entity.QuizType;
import vn.anhtuan.finalProject.repository.ChapterRepository;
import vn.anhtuan.finalProject.repository.QuizRepository;
import vn.anhtuan.finalProject.repository.QuizTypeRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuizAdminServiceTest {

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private QuizTypeRepository quizTypeRepository;

    @Mock
    private ChapterRepository chapterRepository;

    @InjectMocks
    private QuizAdminService quizAdminService;

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

        quiz = new Quiz();
        quiz.setId(1);
        quiz.setCode("Q001");
        quiz.setTitleVi("Bài kiểm tra 1");
        quiz.setChapter(chapter);
        quiz.setQuizType(quizType);
    }

    @Test
    void testCreateQuiz_Success() {
        QuizRequest request = new QuizRequest();
        request.setChapterId(1L);
        request.setQuizTypeId(1);
        request.setCode("Q001");
        request.setTitleVi("Bài kiểm tra 1");
        request.setQuestions(new ArrayList<>());

        when(quizRepository.existsByCode("Q001")).thenReturn(false);
        when(chapterRepository.findById(1L)).thenReturn(Optional.of(chapter));
        when(quizTypeRepository.findById(1)).thenReturn(Optional.of(quizType));
        when(quizRepository.save(any(Quiz.class))).thenReturn(quiz);

        Quiz result = quizAdminService.createQuiz(request);

        assertNotNull(result);
        assertEquals("Q001", result.getCode());
        verify(quizRepository).save(any(Quiz.class));
    }

    @Test
    void testCreateQuiz_DuplicateCode() {
        QuizRequest request = new QuizRequest();
        request.setCode("Q001");

        when(quizRepository.existsByCode("Q001")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> quizAdminService.createQuiz(request));
    }

    @Test
    void testGetQuizById_Success() {
        when(quizRepository.findById(1)).thenReturn(Optional.of(quiz));
        Quiz result = quizAdminService.getQuizById(1);
        assertNotNull(result);
        assertEquals("Q001", result.getCode());
    }

    @Test
    void testDeleteQuiz_Success() {
        when(quizRepository.existsById(1)).thenReturn(true);
        quizAdminService.deleteQuiz(1);
        verify(quizRepository).deleteById(1);
    }
}
