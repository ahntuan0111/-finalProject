package vn.anhtuan.finalProject.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class ChapterRequest {

    @NotNull(message = "ID môn học không được để trống")
    private Integer subjectId;

    @NotBlank(message = "Tên chương tiếng Việt không được để trống")
    private String titleVi;

    private String titleEn; // Tiếng Anh không bắt buộc, nếu chưa dịch thì để trống

    @NotNull(message = "Thứ tự không được để trống")
    private Integer orderNo;

    public ChapterRequest() {
    }

    public ChapterRequest(Integer subjectId, String titleEn, String titleVi, Integer orderNo) {
        this.subjectId = subjectId;
        this.titleEn = titleEn;
        this.titleVi = titleVi;
        this.orderNo = orderNo;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    public String getTitleEn() {
        return titleEn;
    }

    public void setTitleEn(String titleEn) {
        this.titleEn = titleEn;
    }

    public String getTitleVi() {
        return titleVi;
    }

    public void setTitleVi(String titleVi) {
        this.titleVi = titleVi;
    }
}
