package vn.anhtuan.finalProject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.anhtuan.finalProject.dto.response.ApiResponse;
import vn.anhtuan.finalProject.dto.response.NotificationResponse;
import vn.anhtuan.finalProject.entity.User;
import vn.anhtuan.finalProject.service.IUserService;
import vn.anhtuan.finalProject.service.NotificationService;

import java.util.List;

/**
 * Controller thông báo.
 *
 * Nguyên tắc: Controller chỉ nhận Request → gọi Service → trả Response (DTO).
 * Mapping entity → DTO được thực hiện trong NotificationService, không phải ở đây.
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private IUserService userService;

    /**
     * GET /api/notifications
     * Danh sách thông báo của user hiện tại.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getMyNotifications() {
        User user = userService.getCurrentUser();
        List<NotificationResponse> responses = notificationService.getUserNotificationsAsDto(user);
        return ResponseEntity.ok(ApiResponse.success(responses, "Thành công"));
    }

    /**
     * GET /api/notifications/unread-count
     * Số lượng thông báo chưa đọc (hiển thị badge trên chuông).
     */
    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Integer>> getUnreadCount() {
        User user = userService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(notificationService.getUnreadCount(user), "Thành công"));
    }

    /**
     * PUT /api/notifications/{id}/read
     * Đánh dấu 1 thông báo đã đọc.
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable Long id) {
        User user = userService.getCurrentUser();
        notificationService.markAsRead(id, user);
        return ResponseEntity.ok(ApiResponse.success(null, "Đã đánh dấu đọc"));
    }

    /**
     * PUT /api/notifications/read-all
     * Đánh dấu tất cả thông báo đã đọc.
     */
    @PutMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead() {
        User user = userService.getCurrentUser();
        notificationService.markAllAsRead(user);
        return ResponseEntity.ok(ApiResponse.success(null, "Đã đánh dấu tất cả đọc"));
    }
}