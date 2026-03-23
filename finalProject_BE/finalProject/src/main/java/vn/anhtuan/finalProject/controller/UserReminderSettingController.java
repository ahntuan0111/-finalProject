package vn.anhtuan.finalProject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.anhtuan.finalProject.dto.request.ReminderSettingRequest;
import vn.anhtuan.finalProject.dto.response.ApiResponse;
import vn.anhtuan.finalProject.service.UserReminderSettingService;

@RestController
@RequestMapping("/api/users/reminders")
public class UserReminderSettingController {

    @Autowired
    private UserReminderSettingService service;

    @GetMapping
    public ResponseEntity<?> getMySettings() {
        return ResponseEntity.ok(ApiResponse.success(
                service.getMySettings(),
                "Lấy cấu hình nhắc nhở thành công"
        ));
    }

    @PutMapping
    public ResponseEntity<?> updateMySettings(@RequestBody ReminderSettingRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                service.updateMySettings(request),
                "Cập nhật cấu hình nhắc nhở thành công"
        ));
    }
}
