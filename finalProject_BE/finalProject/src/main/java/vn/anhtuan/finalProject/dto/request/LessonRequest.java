package vn.anhtuan.finalProject.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class LessonRequest {
    @NotNull(message = "ID chương không được để trống")
    private Long chapterId;

    @NotBlank(message = "Tiêu đề tiếng Việt không được để trống")
    private String titleVi;

    private String titleEn; // Có thể để trống nếu Admin chưa kịp dịch

    private String videoUrl;
    private Integer orderNo;

    // Danh sách nội dung (đoạn văn, công thức, ảnh)
    private List<LessonContentRequest> contents;

    public LessonRequest() {
    }

    public LessonRequest(Long chapterId, List<LessonContentRequest> contents, String videoUrl, Integer orderNo, String titleEn, String titleVi) {
        this.chapterId = chapterId;
        this.contents = contents;
        this.videoUrl = videoUrl;
        this.orderNo = orderNo;
        this.titleEn = titleEn;
        this.titleVi = titleVi;
    }

    public Long getChapterId() {
        return chapterId;
    }

    public void setChapterId(Long chapterId) {
        this.chapterId = chapterId;
    }

    public String getTitleVi() {
        return titleVi;
    }

    public void setTitleVi(String titleVi) {
        this.titleVi = titleVi;
    }

    public String getTitleEn() {
        return titleEn;
    }

    public void setTitleEn(String titleEn) {
        this.titleEn = titleEn;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    public List<LessonContentRequest> getContents() {
        return contents;
    }

    public void setContents(List<LessonContentRequest> contents) {
        this.contents = contents;
    }
}