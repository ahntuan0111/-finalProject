package vn.anhtuan.finalProject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.anhtuan.finalProject.entity.NotificationType;
import vn.anhtuan.finalProject.entity.User;
import vn.anhtuan.finalProject.repository.UserRepository;
import vn.anhtuan.finalProject.service.NotificationService;

import java.util.Optional;

@RestController
@RequestMapping("/api/test-notify")
public class TestNotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/send")
    public ResponseEntity<String> testSendNotification(
            @RequestParam String username,
            @RequestParam(defaultValue = "REMINDER") NotificationType type) {

        // 1. Tìm user trong Database
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy User: " + username);
        }

        User user = userOpt.get();

        // 2. Nội dung thông báo tuỳ theo Type
        String title;
        String body;

        switch (type) {
            case GAMIFICATION:
                title = "🏆 Chúc mừng " + username + "!";
                body = "Bạn đã đạt chuỗi học tập 7 ngày liên tiếp. Tiếp tục phát huy nhé!";
                break;
            case SYSTEM:
                title = "📚 Bài học mới đã có mặt";
                body = "Thầy giáo vừa tải lên tài liệu mới cho môn Toán học.";
                break;
            default:
                title = "⏳ Nhắc nhở học tập";
                body = "Đã đến giờ vào học rồi, đừng để đứt chuỗi nhé!";
                break;
        }

        // 3. Thực hiện LƯU DATABASE và GỬI PUSH FIREBASE
        // Truyền actionUrl là rỗng ("") để test tạm
        notificationService.createAndSendNotification(user, title, body, type, "");

        return ResponseEntity.ok("Đã tạo và gửi thông báo loại " + type.name() + " cho " + username);
    }
}