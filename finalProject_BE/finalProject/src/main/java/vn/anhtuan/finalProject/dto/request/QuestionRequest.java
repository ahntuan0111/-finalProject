package vn.anhtuan.finalProject.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class QuestionRequest {
    private Long id;
    private String explanationVi;
    private String explanationEn;
    private Integer orderNo; // THÊM
    private List<QuestionContentRequest> contents;
    private List<ChoiceRequest> choices;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExplanationVi() {
        return explanationVi;
    }

    public void setExplanationVi(String explanationVi) {
        this.explanationVi = explanationVi;
    }

    public String getExplanationEn() {
        return explanationEn;
    }

    public void setExplanationEn(String explanationEn) {
        this.explanationEn = explanationEn;
    }

    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    public List<QuestionContentRequest> getContents() {
        return contents;
    }

    public void setContents(List<QuestionContentRequest> contents) {
        this.contents = contents;
    }

    public List<ChoiceRequest> getChoices() {
        return choices;
    }

    public void setChoices(List<ChoiceRequest> choices) {
        this.choices = choices;
    }

}