package vn.anhtuan.finalProject.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import vn.anhtuan.finalProject.entity.Notification;
import vn.anhtuan.finalProject.entity.NotificationType;
import vn.anhtuan.finalProject.entity.User;
import vn.anhtuan.finalProject.service.IUserService;
import vn.anhtuan.finalProject.service.NotificationService;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private IUserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        
        when(userService.getCurrentUser()).thenReturn(testUser);
    }

    @Test
    @WithMockUser
    void testGetMyNotifications() throws Exception {
        Notification notif = new Notification();
        notif.setId(100L);
        notif.setTitle("Test Title");
        notif.setMessage("Test Message");
        notif.setType(NotificationType.SYSTEM);
        notif.setCreatedAt(LocalDateTime.now());

        when(notificationService.getUserNotifications(any(User.class)))
                .thenReturn(Collections.singletonList(notif));

        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].title").value("Test Title"));
    }

    @Test
    @WithMockUser
    void testGetUnreadCount() throws Exception {
        when(notificationService.getUnreadCount(any(User.class))).thenReturn(5);

        mockMvc.perform(get("/api/notifications/unread-count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(5));
    }

    @Test
    @WithMockUser
    void testMarkAsRead() throws Exception {
        mockMvc.perform(put("/api/notifications/100/read"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Đã đánh dấu đọc"));

        verify(notificationService, times(1)).markAsRead(eq(100L), any(User.class));
    }

    @Test
    @WithMockUser
    void testMarkAllAsRead() throws Exception {
        mockMvc.perform(put("/api/notifications/read-all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Đã đánh dấu tất cả đọc"));

        verify(notificationService, times(1)).markAllAsRead(any(User.class));
    }
}
