package vn.anhtuan.finalProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.anhtuan.finalProject.entity.QuizResult;
import vn.anhtuan.finalProject.entity.QuizStatus;

import java.util.List;

public interface QuizResultRepository extends JpaRepository<QuizResult, Long> {

    // Tìm số lần làm bài lớn nhất của User đối với Quiz này
    @Query("SELECT MAX(qr.attemptNo) FROM QuizResult qr WHERE qr.quiz.id = :quizId AND qr.userId = :userId")
    Integer findMaxAttemptNoByQuizIdAndUserId(@Param("quizId") Integer quizId, @Param("userId") Long userId);

    // Lấy 5 bài Quiz ĐÃ HOÀN THÀNH gần đây nhất của user, sắp xếp từ mới nhất -> cũ nhất
    List<QuizResult> findTop5ByUserIdAndStatusOrderByCompletedAtDesc(Long userId, QuizStatus status);
}
