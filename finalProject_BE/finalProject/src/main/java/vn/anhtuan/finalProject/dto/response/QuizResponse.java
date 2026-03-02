package vn.anhtuan.finalProject.dto.response;

import lombok.Data;
import vn.anhtuan.finalProject.entity.Quiz;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class QuizResponse {
    private Integer id;
    private String code;
    private String title;
    private LocalDateTime createdAt;
    private Long chapterId;
    private String chapterTitle;
    private Integer quizTypeId;
    private String quizTypeName;

    // SỬA DÒNG NÀY: Dùng QuestionResponse thay vì Entity Question
    private List<QuestionResponse> questions;

    public static QuizResponse fromEntity(Quiz quiz) {
        QuizResponse response = new QuizResponse();
        response.setId(quiz.getId());
        response.setCode(quiz.getCode());
        response.setTitle(quiz.getTitle());
        response.setCreatedAt(quiz.getCreatedAt());

        if (quiz.getChapter() != null) {
            response.setChapterId(quiz.getChapter().getId());
            response.setChapterTitle(quiz.getChapter().getTitle());
        }

        if (quiz.getQuizType() != null) {
            response.setQuizTypeId(quiz.getQuizType().getId());
            response.setQuizTypeName(quiz.getQuizType().getName());
        }

        // SỬA ĐOẠN NÀY: Dùng luồng (stream) để map từng Question Entity sang QuestionResponse DTO
        if (quiz.getQuestions() != null) {
            response.setQuestions(quiz.getQuestions().stream()
                    .map(QuestionResponse::fromEntity)
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

    public List<QuestionResponse> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionResponse> questions) {
        this.questions = questions;
    }
}