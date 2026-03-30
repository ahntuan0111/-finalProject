package vn.anhtuan.finalProject.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import vn.anhtuan.finalProject.entity.Role;
import vn.anhtuan.finalProject.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional // Tự động rollback sau mỗi test case để DB H2 luôn sạch
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@gmail.com");
        user.setPassword("password123");
        user.setRole(Role.USER);
        userRepository.save(user);
    }

    @Test
    void testFindByUsername_Found() {
        Optional<User> found = userRepository.findByUsername("testuser");
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@gmail.com");
    }

    @Test
    void testExistsByEmail_True() {
        boolean exists = userRepository.existsByEmail("test@gmail.com");
        assertThat(exists).isTrue();
    }

    @Test
    void testCountUserGrowthByDate() {
        // Arrange: Tạo thêm 1 user với ngày tạo cụ thể (nếu DB cho phép set createdAt)
        // Lưu ý: @CreationTimestamp có thể ghi đè, nhưng ở đây ta test câu query có chạy được không
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        
        // Act
        List<Object[]> results = userRepository.countUserGrowthByDate(startDate);
        
        // Assert
        assertThat(results).isNotEmpty();
        // Index 0 là Date, Index 1 là Count
        assertThat(results.get(0)[1]).isNotNull();
    }

    @Test
    void testFindUsersNotActiveToday() {
        List<User> inactiveUsers = userRepository.findUsersNotActiveToday(Role.USER);
        assertThat(inactiveUsers).isNotEmpty(); // user vừa tạo chưa có lastActiveDate nên sẽ lọt vào danh sách này
    }
}
