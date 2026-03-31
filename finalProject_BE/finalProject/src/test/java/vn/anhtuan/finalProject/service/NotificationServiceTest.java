package vn.anhtuan.finalProject.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.anhtuan.finalProject.entity.Notification;
import vn.anhtuan.finalProject.entity.NotificationType;
import vn.anhtuan.finalProject.entity.User;
import vn.anhtuan.finalProject.repository.NotificationRepository;
import vn.anhtuan.finalProject.repository.UserRepository;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationService notificationService;

    private User user;
    private Notification notification;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setFcmToken("mock_token");

        notification = new Notification();
        notification.setId(1L);
        notification.setUser(user);
        notification.setTitle("Title");
        notification.setMessage("Message");
        notification.setRead(false);
    }

    @Test
    void testCreateAndSendNotification() {
        notificationService.createAndSendNotification(user, "Test Title", "Test Msg", NotificationType.REMINDER, "/streak");
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void testGetUserNotifications() {
        when(notificationRepository.findByUserOrderByCreatedAtDesc(user)).thenReturn(Collections.singletonList(notification));
        var result = notificationService.getUserNotifications(user);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void testMarkAsRead() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
        notificationService.markAsRead(1L, user);
        assertTrue(notification.isRead());
        verify(notificationRepository).save(notification);
    }
}
