package vn.anhtuan.finalProject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "lesson_completion", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "lesson_id"})
})
@Data
@Builder
public class LessonCompletion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @Column(name = "is_theory_completed", nullable = false)
    private boolean isTheoryCompleted = false;

    @Column(name = "is_exercise_completed", nullable = false)
    private boolean isExerciseCompleted = false;

    @Column(name = "completed_at")
    private LocalDateTime completedAt; // Bằng NULL nếu chưa xong cả 2

    public LessonCompletion() {
    }

    public LessonCompletion(Long id, User user, Lesson lesson, boolean isTheoryCompleted, boolean isExerciseCompleted, LocalDateTime completedAt) {
        this.id = id;
        this.user = user;
        this.lesson = lesson;
        this.isTheoryCompleted = isTheoryCompleted;
        this.isExerciseCompleted = isExerciseCompleted;
        this.completedAt = completedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isTheoryCompleted() {
        return isTheoryCompleted;
    }

    public void setTheoryCompleted(boolean theoryCompleted) {
        isTheoryCompleted = theoryCompleted;
    }

    public boolean isExerciseCompleted() {
        return isExerciseCompleted;
    }

    public void setExerciseCompleted(boolean exerciseCompleted) {
        isExerciseCompleted = exerciseCompleted;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}
