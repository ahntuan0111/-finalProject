package vn.anhtuan.finalProject.dto.request;

import lombok.Data;

@Data
public class QuestionContentRequest {
    private String contentType;
    private String contentValue;
    private Integer contentOrder; // THÊM

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentValue() {
        return contentValue;
    }

    public void setContentValue(String contentValue) {
        this.contentValue = contentValue;
    }

    public Integer getContentOrder() {
        return contentOrder;
    }

    public void setContentOrder(Integer contentOrder) {
        this.contentOrder = contentOrder;
    }
}