package vn.anhtuan.finalProject.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.anhtuan.finalProject.entity.Notification;
import vn.anhtuan.finalProject.entity.NotificationType;
import vn.anhtuan.finalProject.entity.Role;
import vn.anhtuan.finalProject.entity.User;
import vn.anhtuan.finalProject.repository.NotificationRepository;
import vn.anhtuan.finalProject.repository.UserRepository;

import java.util.List;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    // HÀM 1: Gửi cho 1 cá nhân (Lưu DB + Bắn FCM)
    @Transactional
    public void createAndSendNotification(User user, String title, String message, NotificationType type, String actionUrl) {
        // 1. Lưu DB
        Notification notif = new Notification();
        notif.setUser(user);
        notif.setTitle(title);
        notif.setMessage(message);
        notif.setType(type);
        notif.setActionUrl(actionUrl);
        notificationRepository.save(notif);

        // 2. Gửi Firebase
        if (user.getFcmToken() != null && !user.getFcmToken().isEmpty()) {
            try {
                com.google.firebase.messaging.Notification firebaseNotif =
                        com.google.firebase.messaging.Notification.builder()
                                .setTitle(title)
                                .setBody(message)
                                .build();

                Message fcmMessage = Message.builder()
                        .setToken(user.getFcmToken())
                        .setNotification(firebaseNotif)
                        .putData("actionUrl", actionUrl != null ? actionUrl : "")
                        .putData("type", type.name())
                        .build();

                FirebaseMessaging.getInstance().send(fcmMessage);
            } catch (Exception e) {
                logger.error("Lỗi bắn FCM cho {}: {}", user.getUsername(), e.getMessage());
            }
        }
    }

    // HÀM 2: GỬI HÀNG LOẠT (CHẠY NGẦM) CHO TOÀN BỘ HỌC SINH
    @Async // <- Cực kỳ quan trọng để API không bị đơ
    public void notifyAllStudents(String title, String message, NotificationType type, String actionUrl) {
        logger.info("--- Bắt đầu tiến trình ngầm: Gửi thông báo cho tất cả học sinh ---");

        List<User> students = userRepository.findAllByRole(Role.USER); // Chỉ lấy Học sinh
        int count = 0;

        for (User student : students) {
            try {
                createAndSendNotification(student, title, message, type, actionUrl);
                count++;
            } catch (Exception e) {
                logger.error("Lỗi gửi thông báo cho user {}: {}", student.getUsername(), e.getMessage());
            }
        }

        logger.info("--- Hoàn tất: Đã gửi thông báo cho {} học sinh ---", count);
    }

    // Các hàm phụ trợ khác (Get, Count, MarkRead...)
    public List<Notification> getUserNotifications(User user) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public int getUnreadCount(User user) {
        return notificationRepository.countByUserAndIsReadFalse(user);
    }

    @Transactional
    public void markAsRead(Long notificationId, User user) {
        Notification notif = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông báo"));
        if (notif.getUser().getId().equals(user.getId())) {
            notif.setRead(true);
            notificationRepository.save(notif);
        }
    }

    @Transactional
    public void markAllAsRead(User user) {
        notificationRepository.markAllAsReadByUser(user);
    }
}