package vn.anhtuan.finalProject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "exercise_submissions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @Column(name = "image_url", nullable = false, length = 1000)
    private String imageUrl;

    @Column(name = "is_valid", nullable = false)
    private Boolean isValid = false;

    @Column(name = "ai_reason", columnDefinition = "TEXT")
    private String aiReason;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt = LocalDateTime.now();

    @PrePersist
    public void prePersist() {
        if (submittedAt == null) {
            submittedAt = LocalDateTime.now();
        }
    }
}
