package vn.anhtuan.finalProject.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import vn.anhtuan.finalProject.entity.Notification;
import vn.anhtuan.finalProject.entity.NotificationType;
import vn.anhtuan.finalProject.entity.User;
import vn.anhtuan.finalProject.repository.NotificationRepository;
import vn.anhtuan.finalProject.repository.UserRepository;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationService notificationService;

    private MockedStatic<FirebaseMessaging> mockedFirebaseMessaging;
    private FirebaseMessaging firebaseMessaging;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Mock static FirebaseMessaging.getInstance()
        firebaseMessaging = mock(FirebaseMessaging.class);
        mockedFirebaseMessaging = mockStatic(FirebaseMessaging.class);
        mockedFirebaseMessaging.when(FirebaseMessaging::getInstance).thenReturn(firebaseMessaging);
    }

    @AfterEach
    void tearDown() {
        mockedFirebaseMessaging.close();
    }

    @Test
    void testCreateAndSendNotification_WithFcmToken() throws Exception {
        User user = new User();
        user.setFcmToken("test-token");
        user.setUsername("testuser");

        notificationService.createAndSendNotification(user, "Title", "Message", NotificationType.SYSTEM, "action/url");

        // Verify DB save
        verify(notificationRepository, times(1)).save(any(Notification.class));
        
        // Verify FCM send
        verify(firebaseMessaging, times(1)).send(any(Message.class));
    }

    @Test
    void testCreateAndSendNotification_NoFcmToken() throws Exception {
        User user = new User();
        user.setFcmToken(null);

        notificationService.createAndSendNotification(user, "Title", "Message", NotificationType.SYSTEM, null);

        // Verify DB save
        verify(notificationRepository, times(1)).save(any(Notification.class));
        
        // Verify FCM NOT sent
        verify(firebaseMessaging, never()).send(any(Message.class));
    }

    @Test
    void testMarkAsRead_Success() {
        User user = new User();
        user.setId(1L);
        
        Notification notif = new Notification();
        notif.setId(10L);
        notif.setUser(user);
        notif.setRead(false);

        when(notificationRepository.findById(10L)).thenReturn(Optional.of(notif));

        notificationService.markAsRead(10L, user);

        assert(notif.isRead());
        verify(notificationRepository, times(1)).save(notif);
    }

    @Test
    void testMarkAllAsRead() {
        User user = new User();
        notificationService.markAllAsRead(user);
        verify(notificationRepository, times(1)).markAllAsReadByUser(user);
    }
}
