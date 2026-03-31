package vn.anhtuan.finalProject.dto.response;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ReminderSettingResponse {
    private Long id;
    private Boolean isStudyReminderEnabled;
    private String studyDays; 
    private String studyTime; 
    
    private Boolean isStreakReminderEnabled;
    private String streakTime; 
    
    private Boolean isGoalReminderEnabled;
    private LocalDate targetDate;
    private String targetName;

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
}
