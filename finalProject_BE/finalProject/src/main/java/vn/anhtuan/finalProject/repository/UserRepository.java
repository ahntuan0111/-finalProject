package vn.anhtuan.finalProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.anhtuan.finalProject.entity.Role;
import vn.anhtuan.finalProject.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);

    // Lấy số lượng user online hôm nay (Dựa vào cột last_active_date mà chúng ta đã làm ở tính năng Streak)
    long countByLastActiveDate(LocalDate date);

    // Đếm số lượng user đăng ký trong 1 khoảng thời gian (để tính % tăng trưởng)
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // Lấy danh sách 5 user mới nhất
    List<User> findTop5ByOrderByCreatedAtDesc();

    // Query lấy số lượng user đăng ký theo từng ngày (Cho biểu đồ đường)
    @Query("SELECT DATE(u.createdAt) as date, COUNT(u.id) as count FROM User u WHERE u.createdAt >= :startDate GROUP BY DATE(u.createdAt) ORDER BY DATE(u.createdAt) ASC")
    List<Object[]> countUserGrowthByDate(@Param("startDate") LocalDateTime startDate);

    List<User> findAllByRole(Role role);

    @Query("SELECT u FROM User u WHERE u.role = :role AND (u.lastActiveDate IS NULL OR u.lastActiveDate < CURRENT_DATE)")
    List<User> findUsersNotActiveToday(@Param("role") Role role);

}
