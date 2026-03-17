package vn.anhtuan.finalProject.dto.request;

import lombok.Data;

import vn.anhtuan.finalProject.entity.ContentType;
import java.util.List;

@Data
public class ExerciseRequest {
    private Long lessonId;
    private ContentType questionType;
    private String questionValueVi; // Đề bài Tiếng Việt
    private String questionValueEn; // Đề bài Tiếng Anh
    private Integer orderNo;
    private List<SolutionRequest> solutions;

    public ExerciseRequest() {
    }

    public ExerciseRequest(Long lessonId, ContentType questionType, String questionValueVi, String questionValueEn, Integer orderNo, List<SolutionRequest> solutions) {
        this.lessonId = lessonId;
        this.questionType = questionType;
        this.questionValueVi = questionValueVi;
        this.questionValueEn = questionValueEn;
        this.orderNo = orderNo;
        this.solutions = solutions;
    }

    public Long getLessonId() {
        return lessonId;
    }

    public void setLessonId(Long lessonId) {
        this.lessonId = lessonId;
    }

    public List<SolutionRequest> getSolutions() {
        return solutions;
    }

    public void setSolutions(List<SolutionRequest> solutions) {
        this.solutions = solutions;
    }

    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    public String getQuestionValueEn() {
        return questionValueEn;
    }

    public void setQuestionValueEn(String questionValueEn) {
        this.questionValueEn = questionValueEn;
    }

    public String getQuestionValueVi() {
        return questionValueVi;
    }

    public void setQuestionValueVi(String questionValueVi) {
        this.questionValueVi = questionValueVi;
    }

    public ContentType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(ContentType questionType) {
        this.questionType = questionType;
    }
}