package vn.anhtuan.finalProject.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChapterRequest {
    @NotNull(message = "ID môn học không được để trống")
    private Integer subjectId;

    @NotBlank(message = "Tên chương không được để trống")
    private String title;

    @NotNull(message = "Thứ tự không được để trống")
    private Integer orderNo;

    public ChapterRequest() {
    }

    public ChapterRequest(Integer subjectId, String title, Integer orderNo) {
        this.subjectId = subjectId;
        this.title = title;
        this.orderNo = orderNo;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }
}