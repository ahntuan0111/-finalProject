package vn.anhtuan.finalProject.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.anhtuan.finalProject.entity.Quiz;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizResponse {
    private Integer id;
    private String code;
    private String title;
    private LocalDateTime createdAt;
    private Long chapterId;
    private String chapterTitle;
    private Integer quizTypeId;
    private String quizTypeName;
    private Integer durationMinutes;
    private List<QuestionResponse> questions;

    // Helper chọn ngôn ngữ - Đã sửa lỗi chống Null
    public static String getLocalizedText(String textVi, String textEn, String lang) {
        if ("en".equalsIgnoreCase(lang) && textEn != null && !textEn.trim().isEmpty()) {
            return textEn;
        }
        return textVi != null ? textVi : ""; // Luôn trả về chuỗi rỗng nếu cả 2 đều null
    }

    public static QuizResponse fromEntity(Quiz quiz, String lang) {
        QuizResponse response = new QuizResponse();
        response.setId(quiz.getId());
        response.setCode(quiz.getCode());
        response.setTitle(getLocalizedText(quiz.getTitleVi(), quiz.getTitleEn(), lang));
        response.setCreatedAt(quiz.getCreatedAt());

        if (quiz.getChapter() != null) {
            response.setChapterId(quiz.getChapter().getId());
            response.setChapterTitle(getLocalizedText(quiz.getChapter().getTitleVi(), quiz.getChapter().getTitleEn(), lang));
        }

        if (quiz.getQuizType() != null) {
            response.setQuizTypeId(quiz.getQuizType().getId());
            response.setQuizTypeName(getLocalizedText(quiz.getQuizType().getNameVi(), quiz.getQuizType().getNameEn(), lang));
            response.setDurationMinutes(quiz.getQuizType().getDurationMinutes());
        }

        if (quiz.getQuestions() != null) {
            response.setQuestions(quiz.getQuestions().stream()
                    .map(question -> QuestionResponse.fromEntity(question, lang))
                    .collect(Collectors.toList()));
        }

        return response;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getChapterId() {
        return chapterId;
    }

    public void setChapterId(Long chapterId) {
        this.chapterId = chapterId;
    }

    public String getChapterTitle() {
        return chapterTitle;
    }

    public void setChapterTitle(String chapterTitle) {
        this.chapterTitle = chapterTitle;
    }

    public Integer getQuizTypeId() {
        return quizTypeId;
    }

    public void setQuizTypeId(Integer quizTypeId) {
        this.quizTypeId = quizTypeId;
    }

    public String getQuizTypeName() {
        return quizTypeName;
    }

    public void setQuizTypeName(String quizTypeName) {
        this.quizTypeName = quizTypeName;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public List<QuestionResponse> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionResponse> questions) {
        this.questions = questions;
    }
}