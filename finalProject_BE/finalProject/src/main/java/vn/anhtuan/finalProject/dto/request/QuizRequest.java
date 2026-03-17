package vn.anhtuan.finalProject.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class QuizRequest {
    private Long chapterId;
    private Integer quizTypeId;
    private String code;
    private String titleVi; // Bắt buộc
    private String titleEn; // Tùy chọn
    private List<QuestionRequest> questions;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public String getTitleVi() {
        return titleVi;
    }

    public void setTitleVi(String titleVi) {
        this.titleVi = titleVi;
    }

    public String getTitleEn() {
        return titleEn;
    }

    public void setTitleEn(String titleEn) {
        this.titleEn = titleEn;
    }

    public List<QuestionRequest> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionRequest> questions) {
        this.questions = questions;
    }
}