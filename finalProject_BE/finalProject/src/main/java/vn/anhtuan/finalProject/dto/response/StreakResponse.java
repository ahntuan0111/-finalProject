package vn.anhtuan.finalProject.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class StreakResponse {
    private int currentStreak;      // Chuỗi hiện tại
    private int longestStreak;      // Chuỗi dài nhất lịch sử
    private boolean isStudiedToday; // Hôm nay đã học chưa
    private List<String> activeDates; // Danh sách các ngày đã học (Định dạng YYYY-MM-DD) để vẽ Lịch
    private int totalMinutes;

    public StreakResponse() {
    }

    public StreakResponse(int currentStreak, int longestStreak, boolean isStudiedToday, List<String> activeDates, int totalMinutes) {
        this.currentStreak = currentStreak;
        this.longestStreak = longestStreak;
        this.isStudiedToday = isStudiedToday;
        this.activeDates = activeDates;
        this.totalMinutes = totalMinutes;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
    }

    public int getLongestStreak() {
        return longestStreak;
    }

    public void setLongestStreak(int longestStreak) {
        this.longestStreak = longestStreak;
    }

    public boolean isStudiedToday() {
        return isStudiedToday;
    }

    public void setStudiedToday(boolean studiedToday) {
        isStudiedToday = studiedToday;
    }

    public List<String> getActiveDates() {
        return activeDates;
    }

    public void setActiveDates(List<String> activeDates) {
        this.activeDates = activeDates;
    }
}


