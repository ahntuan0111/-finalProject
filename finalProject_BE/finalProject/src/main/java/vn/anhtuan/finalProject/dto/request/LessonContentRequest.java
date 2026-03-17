package vn.anhtuan.finalProject.dto.request;

import lombok.Data;
import vn.anhtuan.finalProject.entity.ContentType;

@Data
public class LessonContentRequest {
    private ContentType type; // TEXT hoặc IMAGE
    private String valueVi;   // Nội dung Tiếng Việt (Bắt buộc)
    private String valueEn;   // Nội dung Tiếng Anh     // Chứa text thường hoặc chuỗi LaTeX "$$ x^2 $$"
    private Integer order;

    public LessonContentRequest() {
    }

    public LessonContentRequest(ContentType type, Integer order, String valueEn, String valueVi) {
        this.type = type;
        this.order = order;
        this.valueEn = valueEn;
        this.valueVi = valueVi;
    }

    public ContentType getType() {
        return type;
    }

    public void setType(ContentType type) {
        this.type = type;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getValueEn() {
        return valueEn;
    }

    public void setValueEn(String valueEn) {
        this.valueEn = valueEn;
    }

    public String getValueVi() {
        return valueVi;
    }

    public void setValueVi(String valueVi) {
        this.valueVi = valueVi;
    }
}