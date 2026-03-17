package vn.anhtuan.finalProject.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.anhtuan.finalProject.dto.request.QuizRequest;
import vn.anhtuan.finalProject.dto.response.ApiResponse;
import vn.anhtuan.finalProject.dto.response.admin.QuizAdminResponse; // Dùng AdminResponse để giữ full 2 ngôn ngữ
import vn.anhtuan.finalProject.entity.Chapter;
import vn.anhtuan.finalProject.entity.Quiz;
import vn.anhtuan.finalProject.entity.QuizType;
import vn.anhtuan.finalProject.service.QuizAdminService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/quizzes")
public class AdminQuizController {

    private final QuizAdminService quizAdminService;

    public AdminQuizController(QuizAdminService quizAdminService) {
        this.quizAdminService = quizAdminService;
    }

    // C: Create Quiz
    @PostMapping
    public ResponseEntity<?> createQuiz(@RequestBody QuizRequest request) {
        try {
            Quiz createdQuiz = quizAdminService.createQuiz(request);

            // Ép sang QuizAdminResponse để trả về đầy đủ 2 ngôn ngữ
            QuizAdminResponse response = QuizAdminResponse.fromEntity(createdQuiz);
            return ResponseEntity.ok(ApiResponse.success(response, "Tạo bài Quiz thành công!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }

    // R: Read All (Lấy danh sách cho bảng Table)
    @GetMapping
    public ResponseEntity<?> getAllQuizzes() {
        // Dùng hàm getAllQuizzesRaw() mà bạn đã thêm ở Service để lấy Entity thô
        List<Quiz> quizzes = quizAdminService.getAllQuizzesRaw();

        List<QuizAdminResponse> responseList = quizzes.stream()
                .map(QuizAdminResponse::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responseList, "Lấy danh sách Quiz thành công!"));
    }

    // R: Lấy chi tiết bài Quiz để Edit (Dành riêng cho Web Admin)
    @GetMapping("/{id}")
    public ResponseEntity<?> getQuizForAdmin(@PathVariable Integer id) {
        try {
            // Gọi hàm getQuizById() mà bạn đã thêm ở Service
            Quiz quiz = quizAdminService.getQuizById(id);
            QuizAdminResponse response = QuizAdminResponse.fromEntity(quiz);
            return ResponseEntity.ok(ApiResponse.success(response, "Lấy chi tiết bài Quiz thành công!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }

    // U: Update Quiz
    @PutMapping("/{id}")
    public ResponseEntity<?> updateQuiz(@PathVariable Integer id, @RequestBody QuizRequest request) {
        try {
            Quiz updatedQuiz = quizAdminService.updateQuiz(id, request);

            // Ép sang QuizAdminResponse để trả về đầy đủ 2 ngôn ngữ
            QuizAdminResponse response = QuizAdminResponse.fromEntity(updatedQuiz);
            return ResponseEntity.ok(ApiResponse.success(response, "Cập nhật bài Quiz thành công!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }

    // D: Delete Quiz
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteQuiz(@PathVariable Integer id) {
        try {
            quizAdminService.deleteQuiz(id);
            return ResponseEntity.ok(ApiResponse.success(null, "Xóa Quiz thành công!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }

    // =========================================================
    // API PHỤ: Lấy data cho các thẻ Select Box ở Frontend
    // =========================================================
    @GetMapping("/types")
    public ResponseEntity<?> getQuizTypes() {
        List<QuizType> types = quizAdminService.getAllQuizTypes();
        return ResponseEntity.ok(ApiResponse.success(types, "Lấy danh sách Quiz Type thành công!"));
    }

    @GetMapping("/chapters")
    public ResponseEntity<?> getChapters() {
        List<Chapter> chapters = quizAdminService.getAllChapters();
        return ResponseEntity.ok(ApiResponse.success(chapters, "Lấy danh sách Chương học thành công!"));
    }
}