package vn.anhtuan.finalProject.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.anhtuan.finalProject.dto.response.StreakResponse;
import vn.anhtuan.finalProject.entity.User;
import vn.anhtuan.finalProject.entity.UserActivity;
import vn.anhtuan.finalProject.repository.UserActivityRepository;
import vn.anhtuan.finalProject.repository.UserRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserActivityServiceTest {

    @Mock
    private UserActivityRepository userActivityRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private IUserService userService;

    @InjectMocks
    private UserActivityService userActivityService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setCurrentStreak(0);
        user.setLongestStreak(0);
        user.setTotalMinutes(0);
    }

    @Test
    void testLogActivity_FirstTimeToday_IncrementStreak() {
        user.setLastActiveDate(LocalDate.now().minusDays(1));
        user.setCurrentStreak(5);
        
        when(userService.getCurrentUser()).thenReturn(user);
        when(userActivityRepository.findByUserIdAndActivityDate(anyLong(), any(LocalDate.class))).thenReturn(Optional.empty());

        userActivityService.logActivity(10);

        assertEquals(6, user.getCurrentStreak());
        assertEquals(LocalDate.now(), user.getLastActiveDate());
        verify(userActivityRepository).save(any(UserActivity.class));
        verify(userRepository).save(user);
    }

    @Test
    void testLogActivity_AlreadyLoggedToday_NoStreakIncrement() {
        user.setLastActiveDate(LocalDate.now());
        user.setCurrentStreak(5);
        UserActivity activity = new UserActivity(user, LocalDate.now(), 20);

        when(userService.getCurrentUser()).thenReturn(user);
        when(userActivityRepository.findByUserIdAndActivityDate(anyLong(), any(LocalDate.class))).thenReturn(Optional.of(activity));

        userActivityService.logActivity(10);

        assertEquals(5, user.getCurrentStreak());
        assertEquals(30, activity.getMinutesUsed());
        verify(userActivityRepository).save(activity);
    }

    @Test
    void testGetMyStreak_LazyReset() {
        user.setLastActiveDate(LocalDate.now().minusDays(3)); // Missed 2 days
        user.setCurrentStreak(5);

        when(userService.getCurrentUser()).thenReturn(user);
        when(userActivityRepository.findRecentActivityDates(anyLong(), any())).thenReturn(new ArrayList<>());

        StreakResponse result = userActivityService.getMyStreak();

        assertEquals(0, result.getCurrentStreak());
        assertEquals(0, user.getCurrentStreak());
        verify(userRepository).save(user);
    }
}
