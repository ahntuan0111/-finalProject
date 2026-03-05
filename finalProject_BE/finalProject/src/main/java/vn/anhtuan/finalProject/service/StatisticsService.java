package vn.anhtuan.finalProject.service;

import org.springframework.stereotype.Service;
import vn.anhtuan.finalProject.dto.response.DashboardResponse;
import vn.anhtuan.finalProject.entity.QuizResult;
import vn.anhtuan.finalProject.entity.QuizStatus;
import vn.anhtuan.finalProject.entity.User;
import vn.anhtuan.finalProject.repository.ProgressRepository;
import vn.anhtuan.finalProject.repository.QuizResultRepository;
import vn.anhtuan.finalProject.repository.UserActivityRepository;
import vn.anhtuan.finalProject.service.IUserService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class StatisticsService {

    private final IUserService userService;
    private final UserActivityRepository userActivityRepository;
    private final ProgressRepository progressRepository;
    private final QuizResultRepository quizResultRepository; // Đã thêm repo mới

    public StatisticsService(IUserService userService,
                             UserActivityRepository userActivityRepository,
                             ProgressRepository progressRepository,
                             QuizResultRepository quizResultRepository) {
        this.userService = userService;
        this.userActivityRepository = userActivityRepository;
        this.progressRepository = progressRepository;
        this.quizResultRepository = quizResultRepository;
    }

    public DashboardResponse getDashboardData() {
        User user = userService.getCurrentUser();
        LocalDate today = LocalDate.now();

        // 1. TOP STATS
        int totalMins = user.getTotalMinutes() != null ? user.getTotalMinutes() : 0;
        int streak = user.getCurrentStreak() != null ? user.getCurrentStreak() : 0;
        DashboardResponse.TopStats topStats = new DashboardResponse.TopStats(totalMins, streak);

        // 2. PROGRESS TRACK (Đường đua tiến độ)
        List<DashboardResponse.SubjectProgressDto> progressTrack = new ArrayList<>();
        // Giả lập lấy từ DB (Hoặc bạn thay bằng code lấy thực tế)
        progressTrack.add(new DashboardResponse.SubjectProgressDto("Toán học", "#2B58C4", 45.0, 45, 100));
        progressTrack.add(new DashboardResponse.SubjectProgressDto("Khoa học TN", "#4CAF50", 25.0, 20, 80));

        // 3. PERFORMANCE CHART (Biểu đồ điểm từ DB)
        List<DashboardResponse.ChartPointDto> chart = new ArrayList<>();

        // Lấy 5 bài Quiz gần nhất (trả về Mới -> Cũ)
        List<QuizResult> recentQuizzes = quizResultRepository.findTop5ByUserIdAndStatusOrderByCompletedAtDesc(user.getId(), QuizStatus.COMPLETED);

        // Đảo ngược list để vẽ lên biểu đồ từ Cũ -> Mới (Trái sang Phải)
        Collections.reverse(recentQuizzes);

        double totalScoreForAi = 0.0;
        int testCount = 1;

        for (QuizResult qr : recentQuizzes) {
            double score = qr.getScore() != null ? qr.getScore().doubleValue() : 0.0;
            totalScoreForAi += score;
            // Gán nhãn cho biểu đồ: "L1", "L2" (Lần 1, Lần 2...)
            chart.add(new DashboardResponse.ChartPointDto("L" + testCount, score));
            testCount++;
        }

        // Nếu user chưa làm bài test nào, chèn 1 điểm 0 để biểu đồ không bị crash
        if (chart.isEmpty()) {
            chart.add(new DashboardResponse.ChartPointDto("L1", 0.0));
        }

        // 4. AI PREDICTION (Dự đoán điểm)
        // Tính điểm dự đoán dựa trên trung bình cộng các bài quiz gần đây
        double predictedScore = 0.0;
        if (!recentQuizzes.isEmpty()) {
            predictedScore = Math.round((totalScoreForAi / recentQuizzes.size()) * 10.0) / 10.0; // Làm tròn 1 chữ số
        }

        // Lấy số phút đã học trong tuần này (Từ Thứ Hai đến hiện tại)
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        int studiedMinutesThisWeek = userActivityRepository.sumMinutesSince(user.getId(), startOfWeek);
        int targetMinutes = 900; // Đặt mục tiêu 15 giờ/tuần (900 phút)

        DashboardResponse.AiPredictionDto aiDto = new DashboardResponse.AiPredictionDto(predictedScore, targetMinutes, studiedMinutesThisWeek);

        // 5. HEATMAP DATA (Mật độ 3 tháng ~ 90 ngày)
        List<DashboardResponse.HeatmapPointDto> heatmap = new ArrayList<>();
        List<Object[]> rawHeatmap = userActivityRepository.findHeatmapData(user.getId(), today.minusDays(90));

        for (Object[] row : rawHeatmap) {
            String date = row[0].toString();
            int mins = (int) row[1];
            int intensity = 0;
            if (mins > 0 && mins <= 15) intensity = 1;
            else if (mins > 15 && mins <= 30) intensity = 2;
            else if (mins > 30 && mins <= 60) intensity = 3;
            else if (mins > 60) intensity = 4;

            heatmap.add(new DashboardResponse.HeatmapPointDto(date, intensity));
        }

        // 6. TRẢ VỀ RESPONSE
        return new DashboardResponse(
                topStats,
                heatmap,
                aiDto,
                chart,
                progressTrack
        );
    }
}