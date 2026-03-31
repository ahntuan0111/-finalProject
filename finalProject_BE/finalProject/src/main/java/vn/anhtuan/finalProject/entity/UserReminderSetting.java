package vn.anhtuan.finalProject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "user_reminder_settings")
@Data
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

    public UserReminderSetting() {
    }

    public UserReminderSetting(Long id, String targetName, LocalDate targetDate, Boolean isGoalReminderEnabled, String streakTime, Boolean isStreakReminderEnabled, String studyTime, String studyDays, Boolean isStudyReminderEnabled, User user) {
        this.id = id;
        this.targetName = targetName;
        this.targetDate = targetDate;
        this.isGoalReminderEnabled = isGoalReminderEnabled;
        this.streakTime = streakTime;
        this.isStreakReminderEnabled = isStreakReminderEnabled;
        this.studyTime = studyTime;
        this.studyDays = studyDays;
        this.isStudyReminderEnabled = isStudyReminderEnabled;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    public Boolean getGoalReminderEnabled() {
        return isGoalReminderEnabled;
    }

    public void setGoalReminderEnabled(Boolean goalReminderEnabled) {
        isGoalReminderEnabled = goalReminderEnabled;
    }

    public String getStreakTime() {
        return streakTime;
    }

    public void setStreakTime(String streakTime) {
        this.streakTime = streakTime;
    }

    public Boolean getStreakReminderEnabled() {
        return isStreakReminderEnabled;
    }

    public void setStreakReminderEnabled(Boolean streakReminderEnabled) {
        isStreakReminderEnabled = streakReminderEnabled;
    }

    public String getStudyTime() {
        return studyTime;
    }

    public void setStudyTime(String studyTime) {
        this.studyTime = studyTime;
    }

    public String getStudyDays() {
        return studyDays;
    }

    public void setStudyDays(String studyDays) {
        this.studyDays = studyDays;
    }

    public Boolean getStudyReminderEnabled() {
        return isStudyReminderEnabled;
    }

    public void setStudyReminderEnabled(Boolean studyReminderEnabled) {
        isStudyReminderEnabled = studyReminderEnabled;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
