package vn.anhtuan.finalProject.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class LessonRequest {
    @NotNull(message = "ID chương không được để trống")
    private Long chapterId;

    @NotBlank(message = "Tiêu đề bài học không được để trống")
    private String title;

    private String videoUrl;
    private Integer orderNo;

    // Danh sách nội dung (đoạn văn, công thức, ảnh)
    private List<LessonContentRequest> contents;

    public LessonRequest() {
    }

    public LessonRequest(Long chapterId, String title, String videoUrl, List<LessonContentRequest> contents, Integer orderNo) {
        this.chapterId = chapterId;
        this.title = title;
        this.videoUrl = videoUrl;
        this.contents = contents;
        this.orderNo = orderNo;
    }

    public Long getChapterId() {
        return chapterId;
    }

    public void setChapterId(Long chapterId) {
        this.chapterId = chapterId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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