package vn.anhtuan.finalProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.anhtuan.finalProject.entity.Notification;
import vn.anhtuan.finalProject.entity.User;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Lấy danh sách thông báo của 1 user, sắp xếp mới nhất lên đầu
    List<Notification> findByUserOrderByCreatedAtDesc(User user);

    // Đếm số lượng thông báo chưa đọc (để hiển thị số Badge đỏ trên chuông)
    int countByUserAndIsReadFalse(User user);

    // Đánh dấu tất cả là đã đọc cho 1 user
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user = :user AND n.isRead = false")
    void markAllAsReadByUser(User user);
}