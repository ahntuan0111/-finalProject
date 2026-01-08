package vn.anhtuan.finalProject.dto.request;

import vn.anhtuan.finalProject.entity.ContentType;

public class SolutionRequest {
    private ContentType type; // "TEXT" hoặc "IMAGE"
    private String value;
    private Integer order;

    public SolutionRequest() {
    }

    public SolutionRequest(ContentType type, String value, Integer order) {
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
