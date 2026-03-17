package vn.anhtuan.finalProject.dto.response.admin;

import lombok.Data;
import vn.anhtuan.finalProject.entity.Lesson;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class LessonAdminResponse {
    private Long id;
    private String titleVi; // Tiêu đề Tiếng Việt
    private String titleEn; // Tiêu đề Tiếng Anh
    private String videoUrl;
    private Integer orderNo;
    private Long chapterId;

    // Gắn danh sách nội dung bài học và bài tập (Admin)
    private List<LessonContentAdminResponse> contents;
    private List<ExerciseAdminResponse> exercises;

    public static LessonAdminResponse fromEntity(Lesson lesson) {
        LessonAdminResponse res = new LessonAdminResponse();
        res.setId(lesson.getId());
        res.setTitleVi(lesson.getTitleVi());
        res.setTitleEn(lesson.getTitleEn());
        res.setVideoUrl(lesson.getVideoUrl());
        res.setOrderNo(lesson.getOrderNo());

        if (lesson.getChapter() != null) {
            res.setChapterId(lesson.getChapter().getId());
        }

        if (lesson.getContents() != null) {
            res.setContents(lesson.getContents().stream()
                    .map(LessonContentAdminResponse::fromEntity)
                    .collect(Collectors.toList()));
        }

        // Map danh sách bài tập tự luyện (nếu có)
        // LƯU Ý: Đảm bảo trong entity Lesson.java của bạn đã có biến List<Exercise> exercises nhé!
        if (lesson.getExercises() != null) {
            res.setExercises(lesson.getExercises().stream()
                    .map(ExerciseAdminResponse::fromEntity)
                    .collect(Collectors.toList()));
        }
        return res;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ExerciseAdminResponse> getExercises() {
        return exercises;
    }

    public void setExercises(List<ExerciseAdminResponse> exercises) {
        this.exercises = exercises;
    }

    public List<LessonContentAdminResponse> getContents() {
        return contents;
    }

    public void setContents(List<LessonContentAdminResponse> contents) {
        this.contents = contents;
    }

    public Long getChapterId() {
        return chapterId;
    }

    public void setChapterId(Long chapterId) {
        this.chapterId = chapterId;
    }

    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
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