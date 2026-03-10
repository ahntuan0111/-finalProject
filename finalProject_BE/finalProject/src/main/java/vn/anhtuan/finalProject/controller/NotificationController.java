package vn.anhtuan.finalProject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.anhtuan.finalProject.dto.response.ApiResponse;
import vn.anhtuan.finalProject.entity.User;
import vn.anhtuan.finalProject.service.IUserService;
import vn.anhtuan.finalProject.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private IUserService userService; // Dùng hàm getCurrentUser() cực xịn của bạn

    // 1. Lấy danh sách lịch sử thông báo
    @GetMapping
    public ResponseEntity<?> getMyNotifications() {
        User user = userService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(
                notificationService.getUserNotifications(user),
                "Thành công"
        ));
    }

    // 2. Lấy số lượng thông báo chưa đọc (để hiển thị số trên cái Chuông)
    @GetMapping("/unread-count")
    public ResponseEntity<?> getUnreadCount() {
        User user = userService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(
                notificationService.getUnreadCount(user),
                "Thành công"
        ));
    }

    // 3. Đánh dấu 1 thông báo là đã đọc (Khi user click vào xem)
    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        User user = userService.getCurrentUser();
        notificationService.markAsRead(id, user);
        return ResponseEntity.ok(ApiResponse.success(null, "Đã đánh dấu đọc"));
    }

    // 4. Đánh dấu TẤT CẢ là đã đọc (Nút "Đánh dấu tất cả đã đọc" trên UI)
    @PutMapping("/read-all")
    public ResponseEntity<?> markAllAsRead() {
        User user = userService.getCurrentUser();
        notificationService.markAllAsRead(user);
        return ResponseEntity.ok(ApiResponse.success(null, "Đã đánh dấu tất cả đọc"));
    }
}
