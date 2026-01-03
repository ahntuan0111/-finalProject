package vn.anhtuan.finalProject.dto.request;

import lombok.Data;
import vn.anhtuan.finalProject.entity.ContentType;

@Data
public class LessonContentRequest {
    private ContentType type; // TEXT hoặc IMAGE
    private String value;     // Chứa text thường hoặc chuỗi LaTeX "$$ x^2 $$"
    private Integer order;

    public LessonContentRequest() {
    }

    public LessonContentRequest(ContentType type, String value, Integer order) {
        this.type = type;
        this.value = value;
        this.order = order;
    }

    public ContentType getType() {
        return type;
    }

    public void setType(ContentType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
}