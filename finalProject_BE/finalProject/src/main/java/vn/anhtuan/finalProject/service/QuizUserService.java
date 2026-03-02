package vn.anhtuan.finalProject.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.anhtuan.finalProject.dto.request.QuizSubmitRequest;
import vn.anhtuan.finalProject.dto.response.QuizResponse;
import vn.anhtuan.finalProject.entity.Quiz;
import vn.anhtuan.finalProject.entity.QuizResult;
import vn.anhtuan.finalProject.entity.QuizStatus;
import vn.anhtuan.finalProject.repository.QuizRepository;
import vn.anhtuan.finalProject.repository.QuizResultRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuizUserService {
    private final QuizResultRepository quizResultRepository;

    private final QuizRepository quizRepository;

    public QuizUserService(QuizResultRepository quizResultRepository, QuizRepository quizRepository) {
        this.quizResultRepository = quizResultRepository;
        this.quizRepository = quizRepository;
    }

    @Transactional(readOnly = true)
    public List<QuizResponse> getQuizzesForMobile(String subjectCode, Integer grade) {
        // Lấy danh sách các bài Quiz từ DB thông qua custom query
        List<Quiz> quizzes = quizRepository.findQuizzesBySubjectAndGrade(subjectCode, grade);

        // Convert toàn bộ Entity Quiz sang DTO QuizResponse
        return quizzes.stream()
                .map(QuizResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void submitQuizResult(Integer quizId, Long userId, QuizSubmitRequest request) {
        // 1. Tìm bài Quiz
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài Quiz"));

        // 2. Tìm số lần làm bài hiện tại (Attempt No)
        Integer maxAttempt = quizResultRepository.findMaxAttemptNoByQuizIdAndUserId(quizId, userId);
        int currentAttempt = (maxAttempt == null) ? 1 : maxAttempt + 1;

        // 3. Tính điểm (Thang điểm 10)
        double scoreValue = (double) request.getCorrectAnswers() / request.getTotalQuestions() * 10.0;
        BigDecimal score = new BigDecimal(scoreValue).setScale(2, RoundingMode.HALF_UP);

        // 4. Tạo entity và lưu vào DB
        QuizResult result = new QuizResult();
        result.setUserId(userId);
        result.setQuiz(quiz);
        result.setAttemptNo(currentAttempt);
        result.setCorrectAnswers(request.getCorrectAnswers());
        result.setTotalQuestions(request.getTotalQuestions());
        result.setScore(score);
        result.setDurationSeconds(request.getDurationSeconds() != null ? request.getDurationSeconds() : 0);
        // Đảm bảo Enum QuizStatus của bạn có giá trị COMPLETED nhé
        result.setStatus(QuizStatus.COMPLETED);
        result.setCompletedAt(LocalDateTime.now());

        quizResultRepository.save(result);
    }

}