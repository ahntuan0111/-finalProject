package vn.anhtuan.finalProject.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.anhtuan.finalProject.entity.Choice;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChoiceResponse {
    private Long id;
    private String contentType;
    private String contentValue;
    private Boolean correct;

    public static ChoiceResponse fromEntity(Choice choice, String lang) {
        ChoiceResponse response = new ChoiceResponse();
        response.setId(choice.getId());
        response.setContentType(choice.getContentType().name());
        response.setContentValue(QuizResponse.getLocalizedText(choice.getContentValueVi(), choice.getContentValueEn(), lang));
        response.setCorrect(choice.getCorrect());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Boolean getCorrect() {
        return correct;
    }

    public void setCorrect(Boolean correct) {
        this.correct = correct;
    }
}