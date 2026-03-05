package vn.anhtuan.finalProject.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
public class DashboardResponse {
    private TopStats topStats;
    private List<SubjectProgressDto> progressTrack;
    private List<ChartPointDto> performanceChart;
    private AiPredictionDto aiPrediction;
    private List<HeatmapPointDto> heatmap;

    public DashboardResponse(TopStats topStats, List<HeatmapPointDto> heatmap, AiPredictionDto aiPrediction, List<ChartPointDto> performanceChart, List<SubjectProgressDto> progressTrack) {
        this.topStats = topStats;
        this.heatmap = heatmap;
        this.aiPrediction = aiPrediction;
        this.performanceChart = performanceChart;
        this.progressTrack = progressTrack;
    }

    @Data
    public static class TopStats {
        private int totalMinutes;
        private int currentStreak;

        public TopStats(int totalMinutes, int currentStreak) {
            this.totalMinutes = totalMinutes;
            this.currentStreak = currentStreak;
        }

        public int getTotalMinutes() {
            return totalMinutes;
        }

        public void setTotalMinutes(int totalMinutes) {
            this.totalMinutes = totalMinutes;
        }

        public int getCurrentStreak() {
            return currentStreak;
        }

        public void setCurrentStreak(int currentStreak) {
            this.currentStreak = currentStreak;
        }
    }

    @Data
    public static class SubjectProgressDto {
        private String subjectName;
        private int completedLessons;
        private int totalLessons;
        private double progressPercent;
        private String colorHex;

        public SubjectProgressDto() {
        }

        public SubjectProgressDto(String subjectName, String colorHex, double progressPercent, int completedLessons, int totalLessons) {
            this.subjectName = subjectName;
            this.colorHex = colorHex;
            this.progressPercent = progressPercent;
            this.completedLessons = completedLessons;
            this.totalLessons = totalLessons;
        }

        public String getSubjectName() {
            return subjectName;
        }

        public void setSubjectName(String subjectName) {
            this.subjectName = subjectName;
        }

        public int getCompletedLessons() {
            return completedLessons;
        }

        public void setCompletedLessons(int completedLessons) {
            this.completedLessons = completedLessons;
        }

        public int getTotalLessons() {
            return totalLessons;
        }

        public void setTotalLessons(int totalLessons) {
            this.totalLessons = totalLessons;
        }

        public double getProgressPercent() {
            return progressPercent;
        }

        public void setProgressPercent(double progressPercent) {
            this.progressPercent = progressPercent;
        }

        public String getColorHex() {
            return colorHex;
        }

        public void setColorHex(String colorHex) {
            this.colorHex = colorHex;
        }
    }

    @Data
    public static class ChartPointDto {
        private String dateLabel; // Ví dụ: "T1", "T2" hoặc "Tuần 1"
        private double score;     // Điểm số 0-10

        public ChartPointDto(String dateLabel, double score) {
            this.dateLabel = dateLabel;
            this.score = score;
        }

        public String getDateLabel() {
            return dateLabel;
        }

        public void setDateLabel(String dateLabel) {
            this.dateLabel = dateLabel;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }
    }

    @Data
    public static class AiPredictionDto {
        private double predictedScore;
        private int targetMinutes;
        private int studiedMinutesThisWeek;

        public AiPredictionDto(double predictedScore, int studiedMinutesThisWeek, int targetMinutes) {
            this.predictedScore = predictedScore;
            this.studiedMinutesThisWeek = studiedMinutesThisWeek;
            this.targetMinutes = targetMinutes;
        }

        public double getPredictedScore() {
            return predictedScore;
        }

        public void setPredictedScore(double predictedScore) {
            this.predictedScore = predictedScore;
        }

        public int getTargetMinutes() {
            return targetMinutes;
        }

        public void setTargetMinutes(int targetMinutes) {
            this.targetMinutes = targetMinutes;
        }

        public int getStudiedMinutesThisWeek() {
            return studiedMinutesThisWeek;
        }

        public void setStudiedMinutesThisWeek(int studiedMinutesThisWeek) {
            this.studiedMinutesThisWeek = studiedMinutesThisWeek;
        }
    }

    @Data
    public static class HeatmapPointDto {
        private String date;      // YYYY-MM-DD
        private int intensity;    // 0: Không học, 1: Ít, 2: Vừa, 3: Nhiều, 4: Rất nhiều

        public HeatmapPointDto(String date, int intensity) {
            this.date = date;
            this.intensity = intensity;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public int getIntensity() {
            return intensity;
        }

        public void setIntensity(int intensity) {
            this.intensity = intensity;
        }
    }
}