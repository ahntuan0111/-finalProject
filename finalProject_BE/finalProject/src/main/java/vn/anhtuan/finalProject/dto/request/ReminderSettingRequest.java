package vn.anhtuan.finalProject.dto.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ReminderSettingRequest {
    private Boolean isStudyReminderEnabled;
    private String studyDays; // Ví dụ: "MON,TUE,WED"
    private String studyTime; // Ví dụ: "19:30"
    
    private Boolean isStreakReminderEnabled;
    private String streakTime; // Ví dụ: "21:00"
    
    private Boolean isGoalReminderEnabled;
    private LocalDate targetDate;
    private String targetName;

    public ReminderSettingRequest() {
    }

    public ReminderSettingRequest(Boolean isStudyReminderEnabled, String targetName, LocalDate targetDate, Boolean isGoalReminderEnabled, String streakTime, Boolean isStreakReminderEnabled, String studyTime, String studyDays) {
        this.isStudyReminderEnabled = isStudyReminderEnabled;
        this.targetName = targetName;
        this.targetDate = targetDate;
        this.isGoalReminderEnabled = isGoalReminderEnabled;
        this.streakTime = streakTime;
        this.isStreakReminderEnabled = isStreakReminderEnabled;
        this.studyTime = studyTime;
        this.studyDays = studyDays;
    }

    public Boolean getStudyReminderEnabled() {
        return isStudyReminderEnabled;
    }

    public void setStudyReminderEnabled(Boolean studyReminderEnabled) {
        isStudyReminderEnabled = studyReminderEnabled;
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

    public String getStudyDays() {
        return studyDays;
    }

    public void setStudyDays(String studyDays) {
        this.studyDays = studyDays;
    }

    public String getStudyTime() {
        return studyTime;
    }

    public void setStudyTime(String studyTime) {
        this.studyTime = studyTime;
    }
}
