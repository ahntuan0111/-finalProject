package vn.anhtuan.finalProject.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.anhtuan.finalProject.dto.request.QuizSubmitRequest;
import vn.anhtuan.finalProject.dto.response.ApiResponse;
import vn.anhtuan.finalProject.dto.response.QuizResponse;
import vn.anhtuan.finalProject.service.QuizUserService;
import vn.anhtuan.finalProject.service.IUserService;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes") // Base URL cho các API liên quan đến thao tác của User đối với Quiz
public class QuizUserController {

    private final QuizUserService quizUserService;
    private final IUserService userService;

    public QuizUserController(QuizUserService quizUserService, IUserService userService) {
        this.quizUserService = quizUserService;
        this.userService = userService;
    }

    /**
     * API lấy danh sách bài thi dựa trên mã môn và khối lớp
     * Endpoint: GET /api/quizzes/filter?subjectCode=TOAN&grade=7
     */
    @GetMapping("/filter")
    public ResponseEntity<?> getQuizzesByFilter(
            @RequestParam("subjectCode") String subjectCode,
            @RequestParam("grade") Integer grade) {
        try {
            // Gọi tầng Service để lấy data
            List<QuizResponse> quizzes = quizUserService.getQuizzesForMobile(subjectCode, grade);

            // Trả về response chuẩn của hệ thống (bọc trong ApiResponse)
            return ResponseEntity.ok(ApiResponse.success(quizzes, "Lấy danh sách bài kiểm tra thành công!"));
        } catch (Exception e) {
            // Xử lý lỗi nếu có
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "Lỗi khi tải danh sách: " + e.getMessage()));
        }
    }

    @PostMapping("/{quizId}/submit")
    public ResponseEntity<?> submitQuiz(
            @PathVariable Integer quizId,
            @RequestBody QuizSubmitRequest request) {
        try {
            // LẤY ID NGƯỜI DÙNG TỪ SERVICE BẠN VỪA CẬP NHẬT
            Long userId = userService.getCurrentUserId();

            // GỌI LOGIC LƯU BÀI KIỂM TRA
            quizUserService.submitQuizResult(quizId, userId, request);

            return ResponseEntity.ok(ApiResponse.success(null, "Nộp bài thành công!"));
        } catch (Exception e) {
            e.printStackTrace();
            // Check nếu lỗi do chưa đăng nhập (lỗi từ BadRequestException mà ta ném ra ở Service)
            if (e.getMessage() != null && (e.getMessage().contains("chưa đăng nhập") || e.getMessage().contains("hết hạn"))) {
                return ResponseEntity.status(401).body(ApiResponse.error(401, e.getMessage()));
            }
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "Lỗi nộp bài: " + e.getMessage()));
        }
    }
}
