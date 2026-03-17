package vn.anhtuan.finalProject.dto.response.admin;

import lombok.Data;
import vn.anhtuan.finalProject.entity.Choice;

@Data
public class ChoiceAdminResponse {
    private Long id;
    private String contentType;
    private String contentValueVi; // Trả về nguyên bản Vi
    private String contentValueEn; // Trả về nguyên bản En
    private Boolean correct;

    public static ChoiceAdminResponse fromEntity(Choice choice) {
        ChoiceAdminResponse res = new ChoiceAdminResponse();
        res.setId(choice.getId());
        res.setContentType(choice.getContentType().name());
        res.setContentValueVi(choice.getContentValueVi());
        res.setContentValueEn(choice.getContentValueEn());
        res.setCorrect(choice.getCorrect());
        return res;
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

    public String getContentValueVi() {
        return contentValueVi;
    }

    public void setContentValueVi(String contentValueVi) {
        this.contentValueVi = contentValueVi;
    }

    public String getContentValueEn() {
        return contentValueEn;
    }

    public void setContentValueEn(String contentValueEn) {
        this.contentValueEn = contentValueEn;
    }

    public Boolean getCorrect() {
        return correct;
    }

    public void setCorrect(Boolean correct) {
        this.correct = correct;
    }
}
