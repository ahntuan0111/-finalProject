package vn.anhtuan.finalProject.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import vn.anhtuan.finalProject.entity.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class QuizResultRepositoryTest {

    @Autowired
    private QuizResultRepository quizResultRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private UserRepository userRepository;

    private Long userId = 1L;
    private Integer quizId;

    @BeforeEach
    void setUp() {
        // Tạo User
        User user = new User();
        user.setId(userId);
        user.setUsername("testuser");
        user.setEmail("test@gmail.com");
        user.setPassword("password");
        userRepository.save(user);

        // Tạo Quiz (Giả định Chapter/Subject đã có hoặc Mock ID)
        // Lưu ý: Quiz cần Chapter nhưng ta có thể test qua ID nếu QuizRepository cho phép save
        Quiz quiz = new Quiz();
        quiz.setTitleVi("Quiz Test");
        quiz.setCode("QTEST");
        quizRepository.save(quiz);
        quizId = quiz.getId();

        // Tạo Quiz Result 1
        QuizResult res1 = new QuizResult();
        res1.setUserId(userId);
        res1.setQuiz(quiz);
        res1.setAttemptNo(1);
        res1.setScore(new BigDecimal("8.0"));
        res1.setStatus(QuizStatus.COMPLETED);
        res1.setCompletedAt(LocalDateTime.now().minusHours(1));
        quizResultRepository.save(res1);

        // Tạo Quiz Result 2
        QuizResult res2 = new QuizResult();
        res2.setUserId(userId);
        res2.setQuiz(quiz);
        res2.setAttemptNo(2);
        res2.setScore(new BigDecimal("10.0"));
        res2.setStatus(QuizStatus.COMPLETED);
        res2.setCompletedAt(LocalDateTime.now());
        quizResultRepository.save(res2);
    }

    @Test
    void testFindMaxAttemptNoByQuizIdAndUserId() {
        Integer maxAttempt = quizResultRepository.findMaxAttemptNoByQuizIdAndUserId(quizId, userId);
        assertThat(maxAttempt).isEqualTo(2);
    }

    @Test
    void testFindTop5ByUserIdAndStatusOrderByCompletedAtDesc() {
        List<QuizResult> results = quizResultRepository.findTop5ByUserIdAndStatusOrderByCompletedAtDesc(userId, QuizStatus.COMPLETED);
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getAttemptNo()).isEqualTo(2); // Mới nhất ở đầu
    }
}
