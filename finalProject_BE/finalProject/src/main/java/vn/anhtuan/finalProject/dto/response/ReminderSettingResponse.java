package vn.anhtuan.finalProject.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
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
}
