package vn.anhtuan.finalProject.dto.request;

import lombok.Data;

@Data
public class ChoiceRequest {
    private Long id;
    private String contentType; // THÊM
    private String contentValueVi;
    private String contentValueEn; // ĐỔI TÊN
    private Boolean correct;

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

    public Boolean getCorrect() {
        return correct;
    }

    public void setCorrect(Boolean correct) {
        this.correct = correct;
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
}
