package vn.anhtuan.finalProject.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.anhtuan.finalProject.dto.response.StreakResponse;
import vn.anhtuan.finalProject.entity.User;
import vn.anhtuan.finalProject.entity.UserActivity;
import vn.anhtuan.finalProject.repository.UserActivityRepository;
import vn.anhtuan.finalProject.repository.UserRepository;
import vn.anhtuan.finalProject.service.IUserService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserActivityService {

    private final UserActivityRepository userActivityRepository;
    private final UserRepository userRepository; // Inject thêm UserRepository để lưu User
    private final IUserService userService;

    public UserActivityService(UserActivityRepository userActivityRepository, UserRepository userRepository, IUserService userService) {
        this.userActivityRepository = userActivityRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Transactional
    public void logActivity(int minutes) {
        User user = userService.getCurrentUser();
        LocalDate today = LocalDate.now();

        user.setTotalMinutes((user.getTotalMinutes() == null ? 0 : user.getTotalMinutes()) + minutes);

        // 1. Kiểm tra xem hôm nay user ĐÃ HỌC CHƯA?
        Optional<UserActivity> todayActivityOpt = userActivityRepository.findByUserIdAndActivityDate(user.getId(), today);

        if (todayActivityOpt.isPresent()) {
            // NẾU HÔM NAY ĐÃ HỌC RỒI: Chỉ cộng thêm phút vào lịch sử, KHÔNG đụng gì đến Streak
            UserActivity activity = todayActivityOpt.get();
            activity.setMinutesUsed(activity.getMinutesUsed() + minutes);
            userActivityRepository.save(activity);
            userRepository.save(user);
        } else {
            // NẾU ĐÂY LÀ BÀI HỌC ĐẦU TIÊN TRONG NGÀY: Tiến hành cập nhật Streak!
            LocalDate lastActive = user.getLastActiveDate();

            if (lastActive != null && lastActive.equals(today.minusDays(1))) {
                // Kịch bản A: Hôm qua có học -> Nối tiếp chuỗi!
                user.setCurrentStreak(user.getCurrentStreak() + 1);
            } else {
                // Kịch bản B: Hôm qua KHÔNG học (hoặc đây là lần học đầu tiên của tài khoản) -> Khởi tạo lại chuỗi = 1
                user.setCurrentStreak(1);
            }

            // Kiểm tra và phá kỷ lục Chuỗi dài nhất
            if (user.getCurrentStreak() > user.getLongestStreak()) {
                user.setLongestStreak(user.getCurrentStreak());
            }

            // Cập nhật ngày học gần nhất là Hôm nay
            user.setLastActiveDate(today);
            userRepository.save(user);

            // Ghi log vào bảng Activity để vẽ Lịch
            UserActivity newActivity = new UserActivity(user, today, minutes);
            userActivityRepository.save(newActivity);
        }
    }

    @Transactional
    public StreakResponse getMyStreak() {
        User user = userService.getCurrentUser();
        LocalDate today = LocalDate.now();

        // Đọc O(1) trực tiếp từ bảng User
        int currentStreak = user.getCurrentStreak() != null ? user.getCurrentStreak() : 0;
        int longestStreak = user.getLongestStreak() != null ? user.getLongestStreak() : 0;
        int totalMinutes = user.getTotalMinutes() != null ? user.getTotalMinutes() : 0;

        LocalDate lastActive = user.getLastActiveDate();

        // LOGIC "LAZY RESET": Bắt lỗi đứt chuỗi lúc người dùng mở App
        // Nếu ngày học gần nhất là trước ngày hôm qua (tức là đã bỏ lỡ hôm qua)
        // Mà chuỗi hiện tại trong DB vẫn > 0, thì ép nó về 0 và lưu lại DB.
        if (lastActive != null && lastActive.isBefore(today.minusDays(1)) && currentStreak > 0) {
            currentStreak = 0;
            user.setCurrentStreak(0);
            userRepository.save(user); // Cập nhật lại DB cho đúng thực tế
        }

        // Đánh giá xem hôm nay đã học chưa (Để Frontend đổi màu Icon)
        boolean isStudiedToday = (lastActive != null && lastActive.equals(today));

        // Lấy danh sách ngày để vẽ Lịch (Chỉ lấy 60 ngày gần nhất cho nhẹ Database)
        LocalDate startDate = today.minusDays(90);
        List<LocalDate> recentDates = userActivityRepository.findRecentActivityDates(user.getId(), startDate);

        List<String> activeDateStrings = recentDates.stream()
                .map(LocalDate::toString)
                .collect(Collectors.toList());

        return new StreakResponse(currentStreak, longestStreak, isStudiedToday, activeDateStrings, totalMinutes);
    }
}
