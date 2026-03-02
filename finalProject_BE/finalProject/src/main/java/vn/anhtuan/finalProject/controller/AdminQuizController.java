package vn.anhtuan.finalProject.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.anhtuan.finalProject.dto.request.QuizRequest;
import vn.anhtuan.finalProject.dto.response.ApiResponse;
import vn.anhtuan.finalProject.dto.response.QuizResponse;
import vn.anhtuan.finalProject.entity.Chapter;
import vn.anhtuan.finalProject.entity.Quiz;
import vn.anhtuan.finalProject.entity.QuizType;
import vn.anhtuan.finalProject.service.QuizAdminService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/quizzes")
public class AdminQuizController {

    private final QuizAdminService quizAdminService;

    public AdminQuizController(QuizAdminService quizAdminService) {
        this.quizAdminService = quizAdminService;
    }

    // Đã xóa phần constructor viết tay thừa ở đây

    // C: Create Quiz
    @PostMapping
    public ResponseEntity<?> createQuiz(@RequestBody QuizRequest request) {
        try {
            // 1. Lưu xuống DB và nhận lại Entity
            Quiz createdQuiz = quizAdminService.createQuiz(request);

            // 2. CHUYỂN ĐỔI ENTITY SANG DTO TRƯỚC KHI TRẢ VỀ CHO FRONTEND (Cắt đứt vòng lặp JSON)
            QuizResponse response = QuizResponse.fromEntity(createdQuiz);

            // 3. Trả về DTO
            return ResponseEntity.ok(ApiResponse.success(response, "Tạo bài Quiz thành công!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }

    // R: Read All
    @GetMapping
    public ResponseEntity<?> getAllQuizzes() {
        // Lúc này quizAdminService.getAllQuizzes() trả về List<QuizResponse>
        List<QuizResponse> quizzes = quizAdminService.getAllQuizzes();
        return ResponseEntity.ok(ApiResponse.success(quizzes, "Lấy danh sách Quiz thành công!"));
    }

    // D: Delete Quiz
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteQuiz(@PathVariable Integer id) {
        try {
            quizAdminService.deleteQuiz(id);
            return ResponseEntity.ok(ApiResponse.success(null, "Xóa Quiz thành công!"));
        } catch (Exception e) {
            // SỬA Ở ĐÂY: Truyền số 400 (Bad Request) thay vì null
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }

    // Lấy danh sách Quiz Type để hiển thị Select Box ở Frontend
    @GetMapping("/types")
    public ResponseEntity<?> getQuizTypes() {
        List<QuizType> types = quizAdminService.getAllQuizTypes();
        return ResponseEntity.ok(ApiResponse.success(types, "Lấy danh sách Quiz Type thành công!"));
    }

    // Lấy danh sách Chương học để hiển thị Select Box
    @GetMapping("/chapters")
    public ResponseEntity<?> getChapters() {
        List<Chapter> chapters = quizAdminService.getAllChapters();
        return ResponseEntity.ok(ApiResponse.success(chapters, "Lấy danh sách Chương học thành công!"));
    }

    // U: Update Quiz
    @PutMapping("/{id}")
    public ResponseEntity<?> updateQuiz(@PathVariable Integer id, @RequestBody QuizRequest request) {
        try {
            // Gọi Service để cập nhật
            Quiz updatedQuiz = quizAdminService.updateQuiz(id, request);

            // Ép sang DTO để tránh vòng lặp JSON
            QuizResponse response = QuizResponse.fromEntity(updatedQuiz);

            return ResponseEntity.ok(ApiResponse.success(response, "Cập nhật bài Quiz thành công!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }
}