package vn.anhtuan.finalProject.service;

import org.springframework.stereotype.Service;
import vn.anhtuan.finalProject.dto.response.admin.AdminDashboardResponse;
import vn.anhtuan.finalProject.entity.Lesson;
import vn.anhtuan.finalProject.entity.User;
import vn.anhtuan.finalProject.repository.LessonRepository;
import vn.anhtuan.finalProject.repository.QuizRepository;
import vn.anhtuan.finalProject.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdminDashboardService {

    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;
    private final QuizRepository quizRepository;

    public AdminDashboardService(UserRepository userRepository, LessonRepository lessonRepository, QuizRepository quizRepository) {
        this.userRepository = userRepository;
        this.lessonRepository = lessonRepository;
        this.quizRepository = quizRepository;
    }


    // HELPER: Lọc ngôn ngữ chung
    private String getLocalizedText(String textVi, String textEn, String lang) {
        if ("en".equalsIgnoreCase(lang) && textEn != null && !textEn.trim().isEmpty()) {
            return textEn;
        }
        return textVi;
    }

    // THÊM THAM SỐ lang vào hàm
    public AdminDashboardResponse getDashboardData(String lang) {
        AdminDashboardResponse response = new AdminDashboardResponse();
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = LocalDate.now();

        // ==========================================
        // 1. THỐNG KÊ TỔNG QUAN
        // ==========================================
        AdminDashboardResponse.OverviewStats stats = new AdminDashboardResponse.OverviewStats();
        stats.setTotalStudents(userRepository.count());
        stats.setOnlineUsers(userRepository.countByLastActiveDate(today));
        stats.setTotalLessons(lessonRepository.count());
        stats.setTotalExams(quizRepository.count());

        LocalDateTime startOfThisMonth = now.withDayOfMonth(1).withHour(0).withMinute(0);
        LocalDateTime startOfLastMonth = startOfThisMonth.minusMonths(1);

        long usersThisMonth = userRepository.countByCreatedAtBetween(startOfThisMonth, now);
        long usersLastMonth = userRepository.countByCreatedAtBetween(startOfLastMonth, startOfThisMonth.minusSeconds(1));
        stats.setStudentGrowth(calculateGrowthPercent(usersThisMonth, usersLastMonth));

        LocalDateTime startOfThisWeek = now.minusDays(now.getDayOfWeek().getValue() - 1).withHour(0).withMinute(0);
        LocalDateTime startOfLastWeek = startOfThisWeek.minusWeeks(1);

        long lessonsThisWeek = lessonRepository.countByCreatedAtBetween(startOfThisWeek, now);
        long lessonsLastWeek = lessonRepository.countByCreatedAtBetween(startOfLastWeek, startOfThisWeek.minusSeconds(1));
        stats.setLessonGrowth(calculateGrowthPercent(lessonsThisWeek, lessonsLastWeek));

        response.setOverview(stats);

        // ==========================================
        // 2. BIỂU ĐỒ TĂNG TRƯỞNG NGƯỜI DÙNG (30 ngày qua)
        // ==========================================
        List<AdminDashboardResponse.UserGrowthChart> growth = new ArrayList<>();
        LocalDateTime thirtyDaysAgo = now.minusDays(30);
        List<Object[]> rawGrowthData = userRepository.countUserGrowthByDate(thirtyDaysAgo);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        for (Object[] row : rawGrowthData) {
            String dateStr = ((java.sql.Date) row[0]).toLocalDate().format(formatter);
            int count = ((Number) row[1]).intValue();
            growth.add(new AdminDashboardResponse.UserGrowthChart(dateStr, count));
        }
        response.setUserGrowth(growth);

        // ==========================================
        // 3. PHÂN BỔ NỘI DUNG (BIỂU ĐỒ TRÒN)
        // ==========================================
        List<AdminDashboardResponse.ContentDistribution> distribution = new ArrayList<>();
        List<Object[]> rawDistributionData = lessonRepository.countLessonsGroupedBySubject();

        for (Object[] row : rawDistributionData) {
            // LƯU Ý: Nếu query countLessonsGroupedBySubject của bạn dùng SQL raw chọn cột "name",
            // bạn cần vào LessonRepository sửa lại thành chọn "name_vi" nhé.
            String subjectName = (String) row[0];
            int count = ((Number) row[1]).intValue();
            distribution.add(new AdminDashboardResponse.ContentDistribution(subjectName, count));
        }
        response.setContentDistribution(distribution);

        // ==========================================
        // 4. HỌC SINH MỚI ĐĂNG KÝ (5 người gần nhất)
        // ==========================================
        List<AdminDashboardResponse.RecentStudent> students = new ArrayList<>();
        List<User> recentUsers = userRepository.findTop5ByOrderByCreatedAtDesc();
        DateTimeFormatter joinDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (User u : recentUsers) {
            AdminDashboardResponse.RecentStudent s = new AdminDashboardResponse.RecentStudent();
            s.setId(getInitials(u.getUsername()));
            s.setName(u.getUsername());
            s.setGrade("User");
            s.setJoinDate(u.getCreatedAt().format(joinDateFormatter));
            students.add(s);
        }
        response.setRecentStudents(students);

        // ==========================================
        // 5. BÀI HỌC MỚI NHẤT (5 bài gần nhất)
        // ==========================================
        List<AdminDashboardResponse.RecentContent> contents = new ArrayList<>();
        List<Lesson> recentLessons = lessonRepository.findTop5ByOrderByCreatedAtDesc();

        for (Lesson l : recentLessons) {
            AdminDashboardResponse.RecentContent c = new AdminDashboardResponse.RecentContent();
            c.setId(l.getId().toString());

            // SỬA LỖI TẠI ĐÂY: Dùng helper đa ngôn ngữ cho Title (Bài học)
            c.setTitle(getLocalizedText(l.getTitleVi(), l.getTitleEn(), lang));

            // SỬA LỖI TẠI ĐÂY: Dùng helper đa ngôn ngữ cho Name (Môn học)
            c.setSubject(getLocalizedText(l.getChapter().getSubject().getNameVi(), l.getChapter().getSubject().getNameEn(), lang));

            c.setStatus("PUBLISHED");
            contents.add(c);
        }
        response.setRecentContents(contents);

        return response;
    }

    // --- CÁC HÀM PHỤ TRỢ (HELPER METHODS) ---
    private String calculateGrowthPercent(long current, long previous) {
        if (previous == 0) {
            return current > 0 ? "+100%" : "0%";
        }
        double growth = ((double) (current - previous) / previous) * 100;
        long roundedGrowth = Math.round(growth);
        return (roundedGrowth >= 0 ? "+" : "") + roundedGrowth + "%";
    }

    private String getInitials(String name) {
        if (name == null || name.isEmpty()) return "US";
        String[] words = name.trim().split("\\s+");
        if (words.length >= 2) {
            return (words[0].substring(0, 1) + words[1].substring(0, 1)).toUpperCase();
        }
        return name.substring(0, Math.min(name.length(), 2)).toUpperCase();
    }
}