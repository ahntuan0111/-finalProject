package vn.anhtuan.finalProject.dto.request;

import lombok.Data;

@Data
public class QuizSubmitRequest {
    private Integer correctAnswers;
    private Integer totalQuestions;
    private Integer durationSeconds; // Thời gian làm bài (giây)

    public Integer getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(Integer correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public Integer getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public Integer getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(Integer durationSeconds) {
        this.durationSeconds = durationSeconds;
    }
}
