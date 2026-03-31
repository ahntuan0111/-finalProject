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
    
    private String hint1Vi;
    private String hint1En;
    private String hint2Vi;
    private String hint2En;
    private String hint3Vi;
    private String hint3En;

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

    public String getHint1Vi() {
        return hint1Vi;
    }

    public void setHint1Vi(String hint1Vi) {
        this.hint1Vi = hint1Vi;
    }

    public String getHint3En() {
        return hint3En;
    }

    public void setHint3En(String hint3En) {
        this.hint3En = hint3En;
    }

    public String getHint3Vi() {
        return hint3Vi;
    }

    public void setHint3Vi(String hint3Vi) {
        this.hint3Vi = hint3Vi;
    }

    public String getHint2En() {
        return hint2En;
    }

    public void setHint2En(String hint2En) {
        this.hint2En = hint2En;
    }

    public String getHint2Vi() {
        return hint2Vi;
    }

    public void setHint2Vi(String hint2Vi) {
        this.hint2Vi = hint2Vi;
    }

    public String getHint1En() {
        return hint1En;
    }

    public void setHint1En(String hint1En) {
        this.hint1En = hint1En;
    }
}