package vn.anhtuan.finalProject.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.anhtuan.finalProject.dto.request.QuizSubmitRequest;
import vn.anhtuan.finalProject.entity.Quiz;
import vn.anhtuan.finalProject.repository.QuizRepository;
import vn.anhtuan.finalProject.repository.QuizResultRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuizUserServiceTest {

    @Mock
    private QuizResultRepository quizResultRepository;

    @Mock
    private QuizRepository quizRepository;

    @InjectMocks
    private QuizUserService quizUserService;

    private Quiz testQuiz;

    @BeforeEach
    void setUp() {
        testQuiz = new Quiz();
        testQuiz.setId(1);
        testQuiz.setTitleVi("Test Quiz");
    }

    @Test
    void testSubmitQuizResult_Calculation_Success() {
        // Arrange
        QuizSubmitRequest request = new QuizSubmitRequest();
        request.setTotalQuestions(10);
        request.setCorrectAnswers(8);
        request.setDurationSeconds(120);

        when(quizRepository.findById(1)).thenReturn(Optional.of(testQuiz));
        when(quizResultRepository.findMaxAttemptNoByQuizIdAndUserId(1, 100L)).thenReturn(1);

        // Act
        quizUserService.submitQuizResult(1, 100L, request);

        // Assert
        // Điểm dự kiến: (8/10) * 10 = 8.00
        verify(quizResultRepository, times(1)).save(argThat(result -> {
            boolean scoreCorrect = result.getScore().compareTo(new BigDecimal("8.00")) == 0;
            boolean attemptCorrect = result.getAttemptNo() == 2;
            return scoreCorrect && attemptCorrect;
        }));
    }

    @Test
    void testSubmitQuizResult_FirstAttempt_Success() {
        // Arrange
        QuizSubmitRequest request = new QuizSubmitRequest();
        request.setTotalQuestions(5);
        request.setCorrectAnswers(5);

        when(quizRepository.findById(1)).thenReturn(Optional.of(testQuiz));
        when(quizResultRepository.findMaxAttemptNoByQuizIdAndUserId(1, 100L)).thenReturn(null);

        // Act
        quizUserService.submitQuizResult(1, 100L, request);

        // Assert
        verify(quizResultRepository, times(1)).save(argThat(result -> result.getAttemptNo() == 1));
    }
}
