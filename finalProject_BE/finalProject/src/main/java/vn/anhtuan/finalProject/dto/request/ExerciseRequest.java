package vn.anhtuan.finalProject.dto.request;

import lombok.Data;
import vn.anhtuan.finalProject.entity.ContentType;

import java.util.List;

@Data
public class ExerciseRequest {
    private ContentType questionType; // Thêm trường này
    private String questionValue;      // Đổi tên question -> questionValue
    private Long lessonId;
    private Integer orderNo;
    private List<SolutionRequest> solutions;

    public ExerciseRequest() {
    }

    public ExerciseRequest(ContentType questionType, String questionValue, Integer orderNo, Long lessonId, List<SolutionRequest> solutions) {
        this.questionType = questionType;
        this.questionValue = questionValue;
        this.orderNo = orderNo;
        this.lessonId = lessonId;
        this.solutions = solutions;
    }

    public ContentType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(ContentType questionType) {
        this.questionType = questionType;
    }

    public String getQuestionValue() {
        return questionValue;
    }

    public void setQuestionValue(String questionValue) {
        this.questionValue = questionValue;
    }

    public Long getLessonId() {
        return lessonId;
    }

    public void setLessonId(Long lessonId) {
        this.lessonId = lessonId;
    }

    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    public List<SolutionRequest> getSolutions() {
        return solutions;
    }

    public void setSolutions(List<SolutionRequest> solutions) {
        this.solutions = solutions;
    }
}
