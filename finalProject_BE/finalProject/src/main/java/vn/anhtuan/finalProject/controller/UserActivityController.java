package vn.anhtuan.finalProject.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.anhtuan.finalProject.dto.response.ApiResponse;
import vn.anhtuan.finalProject.service.UserActivityService;

@RestController
@RequestMapping("/api/activity")
public class UserActivityController {

    private final UserActivityService userActivityService;

    public UserActivityController(UserActivityService userActivityService) {
        this.userActivityService = userActivityService;
    }

    @PostMapping("/log")
    public ResponseEntity<?> logActivity(@RequestParam(defaultValue = "5") int minutes) {
        userActivityService.logActivity(minutes);
        return ResponseEntity.ok(ApiResponse.success(null, "Ghi nhận hoạt động thành công"));
    }

    @GetMapping("/streak")
    public ResponseEntity<?> getStreak() {
        return ResponseEntity.ok(ApiResponse.success(userActivityService.getMyStreak(), "Thành công"));
    }
}