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
}
