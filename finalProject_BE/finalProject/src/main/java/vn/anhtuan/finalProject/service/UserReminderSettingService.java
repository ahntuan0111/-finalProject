package vn.anhtuan.finalProject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.anhtuan.finalProject.dto.request.ReminderSettingRequest;
import vn.anhtuan.finalProject.dto.response.ReminderSettingResponse;
import vn.anhtuan.finalProject.entity.User;
import vn.anhtuan.finalProject.entity.UserReminderSetting;
import vn.anhtuan.finalProject.repository.UserReminderSettingRepository;

@Service
public class UserReminderSettingService {

    @Autowired
    private UserReminderSettingRepository repository;

    @Autowired
    private IUserService userService;

    public ReminderSettingResponse getMySettings() {
        User user = userService.getCurrentUser();
        UserReminderSetting setting = repository.findByUser(user)
                .orElse(createDefaultSetting(user));
        
        return mapToResponse(setting);
    }

    @Transactional
    public ReminderSettingResponse updateMySettings(ReminderSettingRequest request) {
        User user = userService.getCurrentUser();
        UserReminderSetting setting = repository.findByUser(user)
                .orElse(new UserReminderSetting());
        
        if (setting.getId() == null) {
            setting.setUser(user);
        }

        setting.setIsStudyReminderEnabled(request.getIsStudyReminderEnabled() != null ? request.getIsStudyReminderEnabled() : false);
        setting.setStudyDays(request.getStudyDays());
        setting.setStudyTime(request.getStudyTime());
        
        setting.setIsStreakReminderEnabled(request.getIsStreakReminderEnabled() != null ? request.getIsStreakReminderEnabled() : false);
        setting.setStreakTime(request.getStreakTime());
        
        setting.setIsGoalReminderEnabled(request.getIsGoalReminderEnabled() != null ? request.getIsGoalReminderEnabled() : false);
        setting.setTargetDate(request.getTargetDate());
        setting.setTargetName(request.getTargetName());

        UserReminderSetting saved = repository.save(setting);
        return mapToResponse(saved);
    }

    private UserReminderSetting createDefaultSetting(User user) {
        UserReminderSetting defaultSetting = new UserReminderSetting();
        defaultSetting.setUser(user);
        defaultSetting.setIsStudyReminderEnabled(false);
        defaultSetting.setStudyDays("MON,TUE,WED,THU,FRI,SAT,SUN");
        defaultSetting.setStudyTime("20:00");
        
        defaultSetting.setIsStreakReminderEnabled(false);
        defaultSetting.setStreakTime("21:00");
        
        defaultSetting.setIsGoalReminderEnabled(false);
        return defaultSetting; // Không save vào DB vội cho đến khi User tự sửa
    }

    private ReminderSettingResponse mapToResponse(UserReminderSetting setting) {
        return ReminderSettingResponse.builder()
                .id(setting.getId())
                .isStudyReminderEnabled(setting.getIsStudyReminderEnabled())
                .studyDays(setting.getStudyDays())
                .studyTime(setting.getStudyTime())
                .isStreakReminderEnabled(setting.getIsStreakReminderEnabled())
                .streakTime(setting.getStreakTime())
                .isGoalReminderEnabled(setting.getIsGoalReminderEnabled())
                .targetDate(setting.getTargetDate())
                .targetName(setting.getTargetName())
                .build();
    }
}
