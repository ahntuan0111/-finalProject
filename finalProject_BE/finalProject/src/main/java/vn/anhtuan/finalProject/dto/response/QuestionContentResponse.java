package vn.anhtuan.finalProject.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.anhtuan.finalProject.entity.QuestionContent;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionContentResponse {
    private Long id;
    private String contentType;
    private String contentValue;
    private Integer contentOrder;

    public static QuestionContentResponse fromEntity(QuestionContent qc, String lang) {
        QuestionContentResponse response = new QuestionContentResponse();
        response.setId(qc.getId());
        response.setContentType(qc.getContentType().name());
        response.setContentValue(QuizResponse.getLocalizedText(qc.getContentValueVi(), qc.getContentValueEn(), lang));
        response.setContentOrder(qc.getContentOrder());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getContentOrder() {
        return contentOrder;
    }

    public void setContentOrder(Integer contentOrder) {
        this.contentOrder = contentOrder;
    }

    public String getContentValue() {
        return contentValue;
    }

    public void setContentValue(String contentValue) {
        this.contentValue = contentValue;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}