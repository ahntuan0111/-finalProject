package vn.anhtuan.finalProject.dto.request;

import lombok.Data;

@Data
public class ChoiceRequest {
    private String contentType; // THÊM
    private String contentValue; // ĐỔI TÊN
    private Boolean correct;

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Boolean getCorrect() {
        return correct;
    }

    public void setCorrect(Boolean correct) {
        this.correct = correct;
    }

    public String getContentValue() {
        return contentValue;
    }

    public void setContentValue(String contentValue) {
        this.contentValue = contentValue;
    }
}
