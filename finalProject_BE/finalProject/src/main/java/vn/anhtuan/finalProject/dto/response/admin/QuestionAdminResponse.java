package vn.anhtuan.finalProject.dto.response.admin;

import lombok.Data;
import vn.anhtuan.finalProject.entity.Question;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class QuestionAdminResponse {
    private Long id;
    private String explanationVi; // Lời giải thích Tiếng Việt
    private String explanationEn; // Lời giải thích Tiếng Anh
    private Integer orderNo;

    // Sử dụng list các class AdminResponse bên trên
    private List<QuestionContentAdminResponse> contents;
    private List<ChoiceAdminResponse> choices;

    public static QuestionAdminResponse fromEntity(Question question) {
        QuestionAdminResponse res = new QuestionAdminResponse();
        res.setId(question.getId());
        res.setExplanationVi(question.getExplanationVi());
        res.setExplanationEn(question.getExplanationEn());
        res.setOrderNo(question.getOrderNo());

        if (question.getContents() != null) {
            res.setContents(question.getContents().stream()
                    .map(QuestionContentAdminResponse::fromEntity)
                    .collect(Collectors.toList()));
        }

        if (question.getChoices() != null) {
            res.setChoices(question.getChoices().stream()
                    .map(ChoiceAdminResponse::fromEntity)
                    .collect(Collectors.toList()));
        }

        return res;
    }

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

    public List<QuestionContentAdminResponse> getContents() {
        return contents;
    }

    public void setContents(List<QuestionContentAdminResponse> contents) {
        this.contents = contents;
    }

    public List<ChoiceAdminResponse> getChoices() {
        return choices;
    }

    public void setChoices(List<ChoiceAdminResponse> choices) {
        this.choices = choices;
    }
}
