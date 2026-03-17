package vn.anhtuan.finalProject.dto.request;

import lombok.Data;

@Data
public class QuestionContentRequest {
    private Long id;
    private String contentType;
    private String contentValueVi;
    private String contentValueEn;
    private Integer contentOrder; // THÊM

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

    public Integer getContentOrder() {
        return contentOrder;
    }

    public void setContentOrder(Integer contentOrder) {
        this.contentOrder = contentOrder;
    }
}