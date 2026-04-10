package vn.anhtuan.finalProject.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class SubjectOverviewResponse {
    private Double progressPercent;
    private Long nextLessonId; // ID bài học tiếp theo cần học
    private Integer totalChapters;
    private Integer totalLessons;

    public SubjectOverviewResponse() {
    }

    public SubjectOverviewResponse(Double progressPercent, Long nextLessonId, Integer totalChapters, Integer totalLessons) {
        this.progressPercent = progressPercent;
        this.nextLessonId = nextLessonId;
        this.totalChapters = totalChapters;
        this.totalLessons = totalLessons;
    }

    public Double getProgressPercent() {
        return progressPercent;
    }

    public void setProgressPercent(Double progressPercent) {
        this.progressPercent = progressPercent;
    }

    public Long getNextLessonId() {
        return nextLessonId;
    }

    public void setNextLessonId(Long nextLessonId) {
        this.nextLessonId = nextLessonId;
    }

    public Integer getTotalChapters() {
        return totalChapters;
    }

    public void setTotalChapters(Integer totalChapters) {
        this.totalChapters = totalChapters;
    }

    public Integer getTotalLessons() {
        return totalLessons;
    }

    public void setTotalLessons(Integer totalLessons) {
        this.totalLessons = totalLessons;
    }
}