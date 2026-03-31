package vn.anhtuan.finalProject.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.anhtuan.finalProject.dto.response.admin.AdminDashboardResponse;
import vn.anhtuan.finalProject.dto.response.admin.StudentProfileResponse;
import vn.anhtuan.finalProject.dto.response.ApiResponse;
import vn.anhtuan.finalProject.service.AdminDashboardService;

@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    public AdminDashboardController(AdminDashboardService adminDashboardService) {
        this.adminDashboardService = adminDashboardService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<AdminDashboardResponse>> getDashboardData(
            @RequestHeader(value = "Accept-Language", defaultValue = "vi") String lang) {
        return ResponseEntity.ok(ApiResponse.success(adminDashboardService.getDashboardData(lang), "Lấy dữ liệu Dashboard thành công"));
    }

    @GetMapping("/student/{id}")
    public ResponseEntity<ApiResponse<StudentProfileResponse>> getStudentProfile(
            @PathVariable Long id,
            @RequestHeader(value = "Accept-Language", defaultValue = "vi") String lang) {
        return ResponseEntity.ok(ApiResponse.success(adminDashboardService.getStudentProfile(id, lang), "Lấy hồ sơ học tập thành công"));
    }
}
