package vn.anhtuan.finalProject.dto.response.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfileResponse {

    private HeaderInfo header;
    private MetricsSummary metrics;
    private ChartData charts;
    private List<RecentActivity> activities;
    private List<AssessmentHistory> assessments;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HeaderInfo {
        private String username;
        private String email;
        private String joinDate;
        private String status; // Active, Inactive
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MetricsSummary {
        private int currentStreak;
        private int totalMinutes;
        private long completedLessons;
        private double averageQuizScore;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChartData {
        private List<RadarSkill> radarSkills;
        private List<WeeklyActivity> weeklyActivity;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RadarSkill {
        private String subjectName;
        private double score;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeeklyActivity {
        private String day; // "Mon", "Tue"... hoặc "dd/MM"
        private int minutes;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentActivity {
        private String type; // QUIZ, LESSON, HOMEWORK, AI
        private String action; // "Hoàn thành bài Quiz", "Nộp bài tập"...
        private String title;
        private String time; // "10:30 Hôm nay"
        private String status; // success, processing, warning
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssessmentHistory {
        private Long id;
        private String title;
        private String type;
        private String submittedAt;
        private double score;
        private String status;
    }
}
