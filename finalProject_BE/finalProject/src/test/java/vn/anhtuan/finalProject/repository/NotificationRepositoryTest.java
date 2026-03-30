package vn.anhtuan.finalProject.repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import vn.anhtuan.finalProject.entity.Notification;
import vn.anhtuan.finalProject.entity.NotificationType;
import vn.anhtuan.finalProject.entity.User;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser_notif");
        testUser.setEmail("notif@test.com");
        testUser.setPassword("password");
        userRepository.save(testUser);

        // Tạo 2 thông báo: 1 đã đọc, 1 chưa đọc
        Notification n1 = new Notification();
        n1.setUser(testUser);
        n1.setTitle("Thông báo 1");
        n1.setMessage("Nội dung 1");
        n1.setType(NotificationType.SYSTEM);
        n1.setRead(true);
        notificationRepository.save(n1);

        Notification n2 = new Notification();
        n2.setUser(testUser);
        n2.setTitle("Thông báo 2");
        n2.setMessage("Nội dung 2");
        n2.setType(NotificationType.REMINDER);
        n2.setRead(false);
        notificationRepository.save(n2);
    }

    @Test
    void testFindByUserOrderByCreatedAtDesc() {
        List<Notification> notifications = notificationRepository.findByUserOrderByCreatedAtDesc(testUser);
        assertThat(notifications).hasSize(2);
        // Kiểm tra thứ tự (n2 được tạo sau n1 nên index 0 phải là n2)
        assertThat(notifications.get(0).getTitle()).isEqualTo("Thông báo 2");
    }

    @Test
    void testCountByUserAndIsReadFalse() {
        int unreadCount = notificationRepository.countByUserAndIsReadFalse(testUser);
        assertThat(unreadCount).isEqualTo(1);
    }

    @Test
    void testMarkAllAsReadByUser() {
        notificationRepository.markAllAsReadByUser(testUser);
        
        // Sau khi đánh dấu tất cả, số lượng chưa đọc phải là 0
        int unreadCount = notificationRepository.countByUserAndIsReadFalse(testUser);
        assertThat(unreadCount).isEqualTo(0);
        
        // Kiểm tra lại toàn bộ danh sách xem isRead có true hết không
        List<Notification> all = notificationRepository.findByUserOrderByCreatedAtDesc(testUser);
        assertThat(all).allMatch(Notification::isRead);
    }
}
