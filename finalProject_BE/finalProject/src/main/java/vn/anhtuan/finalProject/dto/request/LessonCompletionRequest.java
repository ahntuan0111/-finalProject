package vn.anhtuan.finalProject.dto.request;

import lombok.Data;
import vn.anhtuan.finalProject.entity.CompletionType;

@Data
public class LessonCompletionRequest {
    private Long lessonId;
    private CompletionType type; // THEORY hoặc EXERCISE

    public LessonCompletionRequest(Long lessonId, CompletionType type) {
        this.lessonId = lessonId;
        this.type = type;
    }

    public CompletionType getType() {
        return type;
    }

    public void setType(CompletionType type) {
        this.type = type;
    }

    public Long getLessonId() {
        return lessonId;
    }

    public void setLessonId(Long lessonId) {
        this.lessonId = lessonId;
    }
}