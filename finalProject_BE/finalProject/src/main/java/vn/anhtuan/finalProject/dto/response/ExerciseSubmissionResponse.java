package vn.anhtuan.finalProject.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseSubmissionResponse {
    private Long id;
    private Long userId;
    private Long exerciseId;
    private String imageUrl;
    private Boolean isValid;
    private String aiReason;
    private LocalDateTime submittedAt;
}
