package vn.anhtuan.finalProject.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vn.anhtuan.finalProject.entity.NotificationType;
import vn.anhtuan.finalProject.entity.Role;
import vn.anhtuan.finalProject.entity.User;
import vn.anhtuan.finalProject.repository.UserRepository;
import vn.anhtuan.finalProject.service.NotificationService;

import java.util.List;

@Component
public class ReminderScheduler {

    private static final Logger logger = LoggerFactory.getLogger(ReminderScheduler.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    // Chạy vào lúc 20:00:00 hàng ngày
    @Scheduled(cron = "0 0 20 * * ?")
    public void sendDailyReminders() {
        logger.info("--- [CRON JOB] Bắt đầu quét và gửi thông báo nhắc nhở học tập (20:00) ---");

        // Tìm những học sinh chưa online/chưa hoàn thành bài học hôm nay
        List<User> lazyStudents = userRepository.findUsersNotActiveToday(Role.USER);

        int count = 0;

        for (User student : lazyStudents) {
            try {
                String title = "⏳ Đừng để đứt chuỗi học tập!";
                String message = "Hôm nay bạn chưa vào học. Chỉ 15 phút thôi, mở App lên học ngay kẻo mất chuỗi nhé!";
                String actionUrl = "home";

                notificationService.createAndSendNotification(
                        student, title, message, NotificationType.REMINDER, actionUrl
                );
                count++;
            } catch (Exception e) {
                logger.error("Lỗi gửi nhắc nhở cho {}: {}", student.getUsername(), e.getMessage());
            }
        }

        logger.info("--- [CRON JOB] Hoàn tất! Đã nhắc nhở {} học sinh chưa học hôm nay ---", count);
    }
}