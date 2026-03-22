package vn.anhtuan.finalProject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.anhtuan.finalProject.dto.response.ApiResponse;
import vn.anhtuan.finalProject.dto.response.ExerciseResponse;
import vn.anhtuan.finalProject.service.ExerciseService;

import java.util.List;

@RestController
@RequestMapping("/api/exercises")
public class ExerciseController {

    @Autowired
    private ExerciseService exerciseService;

    @Autowired
    private vn.anhtuan.finalProject.service.FirebaseStorageService firebaseStorageService;

    // API lấy danh sách bài tập kèm lời giải theo LessonID
    @GetMapping("/by-lesson/{lessonId}")
    public ResponseEntity<?> getExercisesByLesson(
            @PathVariable Long lessonId,
            @RequestHeader(name = "Accept-Language", defaultValue = "vi") String lang // Bổ sung Header ngôn ngữ
    ) {
        // ĐÃ SỬA: Gọi hàm ForMobile để trả về DTO đã lọc ngôn ngữ
        List<ExerciseResponse> exercises = exerciseService.getExercisesByLessonForMobile(lessonId, lang);

        return ResponseEntity.ok(ApiResponse.success(exercises, "Lấy danh sách bài tập thành công"));
    }

    @PostMapping("/{exerciseId}/validate-image")
    public ResponseEntity<?> validateImage(
            @PathVariable Long exerciseId,
            @RequestBody vn.anhtuan.finalProject.dto.request.ValidationSubmissionRequest request,
            @RequestHeader(name = "Accept-Language", defaultValue = "vi") String lang
    ) {
        vn.anhtuan.finalProject.service.GeminiValidationService.GeminiValidationResult result = 
                exerciseService.validateImage(exerciseId, request.getBase64Image(), lang);
        
        if (!result.isValid) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, result.aiReason));
        }
        return ResponseEntity.ok(ApiResponse.success(result, "Hình ảnh hợp lệ"));
    }

    // Signed URL cho Firebase Storage Upload
    @GetMapping("/{exerciseId}/upload-url")
    public ResponseEntity<?> getUploadUrl(@PathVariable Long exerciseId) {
        try {
            var urlData = firebaseStorageService.generateSignedUploadUrl(exerciseId);
            return ResponseEntity.ok(ApiResponse.success(urlData, "Tạo URL upload thành công"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error(500, "Lỗi tạo URL: " + e.getMessage()));
        }
    }

    @PostMapping("/{exerciseId}/save-submission")
    public ResponseEntity<?> saveSubmission(
            @PathVariable Long exerciseId,
            @RequestBody vn.anhtuan.finalProject.dto.request.SaveSubmissionRequest request
    ) {
        try {
            var response = exerciseService.saveSubmission(exerciseId, request);
            return ResponseEntity.ok(ApiResponse.success(response, "Lưu bài tập thành công"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "Lỗi: " + e.getMessage()));
        }
    }
}