package vn.anhtuan.finalProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.anhtuan.finalProject.entity.User;
import vn.anhtuan.finalProject.entity.UserReminderSetting;

import java.util.Optional;

@Repository
public interface UserReminderSettingRepository extends JpaRepository<UserReminderSetting, Long> {
    Optional<UserReminderSetting> findByUser(User user);
}
