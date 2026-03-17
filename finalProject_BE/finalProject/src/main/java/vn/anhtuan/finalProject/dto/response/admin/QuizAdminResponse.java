package vn.anhtuan.finalProject.dto.response.admin;

import lombok.Data;
import vn.anhtuan.finalProject.entity.Quiz;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class QuizAdminResponse {
    private Integer id;
    private String code;
    private String titleVi; // Tiêu đề Tiếng Việt
    private String titleEn; // Tiêu đề Tiếng Anh
    private LocalDateTime createdAt;
    private Long chapterId;
    private Integer quizTypeId;
    private Integer durationMinutes;

    // Gắn danh sách câu hỏi Admin
    private List<QuestionAdminResponse> questions;

    public static QuizAdminResponse fromEntity(Quiz quiz) {
        QuizAdminResponse res = new QuizAdminResponse();
        res.setId(quiz.getId());
        res.setCode(quiz.getCode());
        res.setTitleVi(quiz.getTitleVi());
        res.setTitleEn(quiz.getTitleEn());
        res.setCreatedAt(quiz.getCreatedAt());

        // Admin Edit thường chỉ cần gửi ID lên lại là đủ, nên ta chỉ map ID
        if (quiz.getChapter() != null) {
            res.setChapterId(quiz.getChapter().getId());
        }

        if (quiz.getQuizType() != null) {
            res.setQuizTypeId(quiz.getQuizType().getId());
            res.setDurationMinutes(quiz.getQuizType().getDurationMinutes());
        }

        if (quiz.getQuestions() != null) {
            res.setQuestions(quiz.getQuestions().stream()
                    .map(QuestionAdminResponse::fromEntity)
                    .collect(Collectors.toList()));
        }

        return res;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<QuestionAdminResponse> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionAdminResponse> questions) {
        this.questions = questions;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Integer getQuizTypeId() {
        return quizTypeId;
    }

    public void setQuizTypeId(Integer quizTypeId) {
        this.quizTypeId = quizTypeId;
    }

    public Long getChapterId() {
        return chapterId;
    }

    public void setChapterId(Long chapterId) {
        this.chapterId = chapterId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getTitleEn() {
        return titleEn;
    }

    public void setTitleEn(String titleEn) {
        this.titleEn = titleEn;
    }

    public String getTitleVi() {
        return titleVi;
    }

    public void setTitleVi(String titleVi) {
        this.titleVi = titleVi;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}