package vn.anhtuan.finalProject.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class SubjectOverviewResponse {
    private Double progressPercent;
    private Long nextLessonId; // ID bài học tiếp theo cần học

    public SubjectOverviewResponse() {
    }

    public SubjectOverviewResponse(Double progressPercent, Long nextLessonId) {
        this.progressPercent = progressPercent;
        this.nextLessonId = nextLessonId;
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
}