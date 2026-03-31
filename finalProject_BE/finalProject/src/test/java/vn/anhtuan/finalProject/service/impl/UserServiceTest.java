package vn.anhtuan.finalProject.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.anhtuan.finalProject.entity.StudentProfile;
import vn.anhtuan.finalProject.entity.User;
import vn.anhtuan.finalProject.repository.StudentProfileRepository;
import vn.anhtuan.finalProject.repository.UserRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private StudentProfileRepository profileRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRecordDailyActivity_FirstTime() {
        String username = "testuser";
        User user = new User();
        user.setUsername(username);
        user.setLastActiveDate(null);
        user.setCurrentStreak(0);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        userService.recordDailyActivity(username);

        assertThat(user.getCurrentStreak()).isEqualTo(1);
        assertThat(user.getLastActiveDate()).isEqualTo(LocalDate.now());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testRecordDailyActivity_ConsecutiveDay() {
        String username = "testuser";
        User user = new User();
        user.setUsername(username);
        user.setLastActiveDate(LocalDate.now().minusDays(1));
        user.setCurrentStreak(5);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        userService.recordDailyActivity(username);

        assertThat(user.getCurrentStreak()).isEqualTo(6);
        assertThat(user.getLastActiveDate()).isEqualTo(LocalDate.now());
    }

    @Test
    void testRecordDailyActivity_SkippedDay() {
        String username = "testuser";
        User user = new User();
        user.setUsername(username);
        user.setLastActiveDate(LocalDate.now().minusDays(3));
        user.setCurrentStreak(10);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        userService.recordDailyActivity(username);

        assertThat(user.getCurrentStreak()).isEqualTo(1); // Reset to 1
        assertThat(user.getLastActiveDate()).isEqualTo(LocalDate.now());
    }

    @Test
    void testUpdateGrade_NewProfile() {
        String username = "testuser";
        User user = new User();
        user.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(profileRepository.findByUser(user)).thenReturn(Optional.empty());

        userService.updateGrade(username, 7);

        verify(profileRepository, times(1)).save(any(StudentProfile.class));
    }

    @Test
    void testUpdateGrade_ExistingProfile() {
        String username = "testuser";
        User user = new User();
        user.setUsername(username);
        
        StudentProfile profile = new StudentProfile(user, 6);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(profileRepository.findByUser(user)).thenReturn(Optional.of(profile));

        userService.updateGrade(username, 9);

        assertThat(profile.getCurrentGrade()).isEqualTo(9);
        verify(profileRepository, times(1)).save(profile);
    }
}
