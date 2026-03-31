package vn.anhtuan.finalProject.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
public class ExerciseSubmissionResponse {
    private Long id;
    private Long userId;
    private Long exerciseId;
    private String imageUrl;
    private Boolean isValid;
    private String aiReason;
    private LocalDateTime submittedAt;

    public ExerciseSubmissionResponse(Long id, LocalDateTime submittedAt, String aiReason, Boolean isValid, String imageUrl, Long exerciseId, Long userId) {
        this.id = id;
        this.submittedAt = submittedAt;
        this.aiReason = aiReason;
        this.isValid = isValid;
        this.imageUrl = imageUrl;
        this.exerciseId = exerciseId;
        this.userId = userId;
    }

    public ExerciseSubmissionResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public String getAiReason() {
        return aiReason;
    }

    public void setAiReason(String aiReason) {
        this.aiReason = aiReason;
    }

    public Boolean getValid() {
        return isValid;
    }

    public void setValid(Boolean valid) {
        isValid = valid;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(Long exerciseId) {
        this.exerciseId = exerciseId;
    }
}
