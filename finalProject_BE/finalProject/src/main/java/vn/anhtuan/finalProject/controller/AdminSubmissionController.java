package vn.anhtuan.finalProject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.anhtuan.finalProject.dto.response.ApiResponse;
import vn.anhtuan.finalProject.dto.response.ExerciseSubmissionResponse;
import vn.anhtuan.finalProject.service.SubmissionService;

import java.util.List;

/**
 * Controller quản lý bài nộp cho Admin.
 *
 * Nguyên tắc: Controller chỉ nhận Request → gọi Service → trả Response.
 * KHÔNG inject Repository, KHÔNG chứa logic business, KHÔNG trả entity thô.
 */
@RestController
@RequestMapping("/api/admin/submissions")
@PreAuthorize("hasRole('ADMIN')")
public class AdminSubmissionController {

    @Autowired
    private SubmissionService submissionService;

    /**
     * GET /api/admin/submissions
     * Lấy toàn bộ bài nộp. Trả List<ExerciseSubmissionResponse> — không bao giờ trả entity.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ExerciseSubmissionResponse>>> getAllSubmissions() {
        List<ExerciseSubmissionResponse> submissions = submissionService.getAllSubmissions();
        return ResponseEntity.ok(ApiResponse.success(submissions, "Lấy danh sách bài nộp thành công"));
    }
}
