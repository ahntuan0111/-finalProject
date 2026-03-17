package vn.anhtuan.finalProject.dto.response.admin;

import lombok.Data;
import vn.anhtuan.finalProject.entity.QuestionContent;

@Data
public class QuestionContentAdminResponse {
    private Long id;
    private String contentType;
    private String contentValueVi; // Trả về nguyên bản Vi
    private String contentValueEn; // Trả về nguyên bản En
    private Integer contentOrder;

    public static QuestionContentAdminResponse fromEntity(QuestionContent qc) {
        QuestionContentAdminResponse res = new QuestionContentAdminResponse();
        res.setId(qc.getId());
        res.setContentType(qc.getContentType().name());
        res.setContentValueVi(qc.getContentValueVi());
        res.setContentValueEn(qc.getContentValueEn());
        res.setContentOrder(qc.getContentOrder());
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

    public Integer getContentOrder() {
        return contentOrder;
    }

    public void setContentOrder(Integer contentOrder) {
        this.contentOrder = contentOrder;
    }
}
