package vn.anhtuan.finalProject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "user_reminder_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserReminderSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // Lịch học của tôi
    @Column(name = "is_study_reminder_enabled")
    private Boolean isStudyReminderEnabled = false;

    @Column(name = "study_days")
    private String studyDays; // Lưu dạng "MON,TUE,WED"

    @Column(name = "study_time")
    private String studyTime; // Lưu dạng "HH:mm" (ví dụ "19:30")

    // Cảnh báo mất lửa (Streak)
    @Column(name = "is_streak_reminder_enabled")
    private Boolean isStreakReminderEnabled = false;

    @Column(name = "streak_time")
    private String streakTime; // Lưu dạng "HH:mm" (ví dụ "21:00")

    // Mục tiêu sắp tới (Countdown)
    @Column(name = "is_goal_reminder_enabled")
    private Boolean isGoalReminderEnabled = false;

    @Column(name = "target_date")
    private LocalDate targetDate; // Ngày đến hạn

    @Column(name = "target_name")
    private String targetName; // Tên kỳ thi / mục tiêu
}
