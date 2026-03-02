package vn.anhtuan.finalProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.anhtuan.finalProject.entity.QuizResult;

public interface QuizResultRepository extends JpaRepository<QuizResult, Long> {

    // Tìm số lần làm bài lớn nhất của User đối với Quiz này
    @Query("SELECT MAX(qr.attemptNo) FROM QuizResult qr WHERE qr.quiz.id = :quizId AND qr.userId = :userId")
    Integer findMaxAttemptNoByQuizIdAndUserId(@Param("quizId") Integer quizId, @Param("userId") Long userId);
}
