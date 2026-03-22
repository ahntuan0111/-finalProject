package vn.anhtuan.finalProject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.anhtuan.finalProject.dto.response.ApiResponse;
import vn.anhtuan.finalProject.repository.ExerciseSubmissionRepository;
import vn.anhtuan.finalProject.entity.ExerciseSubmission;
import java.util.List;

@RestController
@RequestMapping("/api/admin/submissions")
public class AdminSubmissionController {

    @Autowired
    private ExerciseSubmissionRepository submissionRepository;

    @GetMapping
    public ResponseEntity<?> getAllSubmissions() {
        // Lấy tất cả bài nộp (Có thể thêm phân trang sau)
        List<ExerciseSubmission> submissions = submissionRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success(submissions, "Lấy danh sách bài nộp thành công"));
    }
}
