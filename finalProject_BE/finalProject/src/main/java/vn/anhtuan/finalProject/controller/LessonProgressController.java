package vn.anhtuan.finalProject.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.anhtuan.finalProject.dto.request.LessonCompletionRequest;
import vn.anhtuan.finalProject.dto.response.ApiResponse;
import vn.anhtuan.finalProject.service.ILessonProgressService;

@RestController
@RequestMapping("/api/progress")
public class LessonProgressController {

    private final ILessonProgressService lessonProgressService;

    public LessonProgressController(ILessonProgressService lessonProgressService) {
        this.lessonProgressService = lessonProgressService;
    }

    @PostMapping("/mark-complete")
    public ResponseEntity<?> markLessonAsCompleted(@RequestBody LessonCompletionRequest request) {
        try {
            lessonProgressService.markAsCompleted(request);
            return ResponseEntity.ok(ApiResponse.success(null, "Cập nhật tiến trình thành công!"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "Lỗi cập nhật: " + e.getMessage()));
        }
    }

    // THÊM API MỚI Ở ĐÂY
    @GetMapping("/my-progress")
    public ResponseEntity<?> getMyProgress(@RequestParam("grade") Integer grade) {
        try {
            // Lấy danh sách tiến trình và trả về
            var result = lessonProgressService.getMyProgress(grade);
            return ResponseEntity.ok(ApiResponse.success(result, "Lấy tiến trình thành công!"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "Lỗi lấy tiến trình: " + e.getMessage()));
        }
    }

    @GetMapping("/overview")
    public ResponseEntity<?> getSubjectOverview(
            @RequestParam("subjectCode") String subjectCode,
            @RequestParam("grade") Integer grade) {
        try {
            var result = lessonProgressService.getSubjectOverview(subjectCode, grade);
            return ResponseEntity.ok(ApiResponse.success(result, "Thành công"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "Lỗi: " + e.getMessage()));
        }
    }
}
