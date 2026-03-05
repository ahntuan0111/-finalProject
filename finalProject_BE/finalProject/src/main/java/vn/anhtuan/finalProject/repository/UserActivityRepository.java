package vn.anhtuan.finalProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.anhtuan.finalProject.entity.UserActivity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {

    Optional<UserActivity> findByUserIdAndActivityDate(Long userId, LocalDate date);

    // CHỈ LẤY NHỮNG NGÀY TRONG KHOẢNG THỜI GIAN NHẤT ĐỊNH (Ví dụ: 60 ngày gần đây để vẽ Lịch)
    @Query("SELECT u.activityDate FROM UserActivity u WHERE u.user.id = :userId AND u.activityDate >= :startDate ORDER BY u.activityDate DESC")
    List<LocalDate> findRecentActivityDates(@Param("userId") Long userId, @Param("startDate") LocalDate startDate);

    @Query("SELECT u.activityDate, u.minutesUsed FROM UserActivity u WHERE u.user.id = :userId AND u.activityDate >= :startDate ORDER BY u.activityDate ASC")
    List<Object[]> findHeatmapData(@Param("userId") Long userId, @Param("startDate") LocalDate startDate);

    @Query("SELECT COALESCE(SUM(u.minutesUsed), 0) FROM UserActivity u WHERE u.user.id = :userId AND u.activityDate >= :startDate")
    Integer sumMinutesSince(@Param("userId") Long userId, @Param("startDate") LocalDate startDate);
}
