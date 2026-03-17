package vn.anhtuan.finalProject.dto.response.admin;

import lombok.Data;
import vn.anhtuan.finalProject.entity.LessonContent;

@Data
public class LessonContentAdminResponse {
    private Long id;
    private String contentType; // "TEXT" hoặc "IMAGE"
    private String contentValueVi; // Trả về nguyên bản Vi
    private String contentValueEn; // Trả về nguyên bản En
    private Integer contentOrder;

    public static LessonContentAdminResponse fromEntity(LessonContent content) {
        LessonContentAdminResponse res = new LessonContentAdminResponse();
        res.setId(content.getId());
        res.setContentType(content.getContentType().name());
        res.setContentValueVi(content.getContentValueVi());
        res.setContentValueEn(content.getContentValueEn());
        res.setContentOrder(content.getContentOrder());
        return res;
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

    public String getContentValueEn() {
        return contentValueEn;
    }

    public void setContentValueEn(String contentValueEn) {
        this.contentValueEn = contentValueEn;
    }

    public String getContentValueVi() {
        return contentValueVi;
    }

    public void setContentValueVi(String contentValueVi) {
        this.contentValueVi = contentValueVi;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}