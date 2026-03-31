package vn.anhtuan.finalProject.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.anhtuan.finalProject.dto.request.ReminderSettingRequest;
import vn.anhtuan.finalProject.dto.response.ReminderSettingResponse;
import vn.anhtuan.finalProject.entity.User;
import vn.anhtuan.finalProject.entity.UserReminderSetting;
import vn.anhtuan.finalProject.repository.UserReminderSettingRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserReminderSettingServiceTest {

    @Mock
    private UserReminderSettingRepository repository;

    @Mock
    private IUserService userService;

    @InjectMocks
    private UserReminderSettingService userReminderSettingService;

    private User user;
    private UserReminderSetting setting;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        setting = new UserReminderSetting();
        setting.setId(1L);
        setting.setUser(user);
        setting.setStudyReminderEnabled(true);
    }

    @Test
    void testGetMySettings_Existing() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(repository.findByUser(user)).thenReturn(Optional.of(setting));

        ReminderSettingResponse result = userReminderSettingService.getMySettings();

        assertNotNull(result);
        assertTrue(result.getStudyReminderEnabled());
    }

    @Test
    void testGetMySettings_CreateDefault() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(repository.findByUser(user)).thenReturn(Optional.empty());

        ReminderSettingResponse result = userReminderSettingService.getMySettings();

        assertNotNull(result);
        assertFalse(result.getStudyReminderEnabled()); // Default is false
    }

    @Test
    void testUpdateMySettings() {
        ReminderSettingRequest request = new ReminderSettingRequest();
        request.setStudyReminderEnabled(true);
        request.setStudyTime("21:00");
        
        when(userService.getCurrentUser()).thenReturn(user);
        when(repository.findByUser(user)).thenReturn(Optional.of(setting));
        when(repository.save(any(UserReminderSetting.class))).thenReturn(setting);

        ReminderSettingResponse result = userReminderSettingService.updateMySettings(request);

        assertNotNull(result);
        verify(repository).save(any(UserReminderSetting.class));
    }
}
