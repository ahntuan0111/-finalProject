package vn.anhtuan.finalProject.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.anhtuan.finalProject.entity.Question;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponse {
    private Long id;
    private String explanation;
    private Integer orderNo;
    private List<QuestionContentResponse> contents;
    private List<ChoiceResponse> choices;

    public static QuestionResponse fromEntity(Question question, String lang) {
        QuestionResponse response = new QuestionResponse();
        response.setId(question.getId());
        response.setExplanation(QuizResponse.getLocalizedText(question.getExplanationVi(), question.getExplanationEn(), lang));
        response.setOrderNo(question.getOrderNo());

        if (question.getContents() != null) {
            response.setContents(question.getContents().stream()
                    .map(c -> QuestionContentResponse.fromEntity(c, lang))
                    .collect(Collectors.toList()));
        }

        if (question.getChoices() != null) {
            response.setChoices(question.getChoices().stream()
                    .map(ch -> ChoiceResponse.fromEntity(ch, lang))
                    .collect(Collectors.toList()));
        }

        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ChoiceResponse> getChoices() {
        return choices;
    }

    public void setChoices(List<ChoiceResponse> choices) {
        this.choices = choices;
    }

    public List<QuestionContentResponse> getContents() {
        return contents;
    }

    public void setContents(List<QuestionContentResponse> contents) {
        this.contents = contents;
    }

    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}
