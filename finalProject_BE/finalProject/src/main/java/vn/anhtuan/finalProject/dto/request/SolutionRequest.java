package vn.anhtuan.finalProject.dto.request;

import lombok.Data;
import vn.anhtuan.finalProject.entity.ContentType;
@Data
public class SolutionRequest {
    private ContentType type;
    private String valueVi; // Lời giải Tiếng Việt
    private String valueEn; // Lời giải Tiếng Anh
    private Integer order;

    public SolutionRequest() {
    }

    public SolutionRequest(String valueEn, Integer order, String valueVi, ContentType type) {
        this.valueEn = valueEn;
        this.order = order;
        this.valueVi = valueVi;
        this.type = type;
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
