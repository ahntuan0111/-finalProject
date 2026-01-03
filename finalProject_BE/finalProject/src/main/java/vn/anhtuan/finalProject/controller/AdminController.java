package vn.anhtuan.finalProject.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.anhtuan.finalProject.dto.request.ChapterRequest;
import vn.anhtuan.finalProject.dto.request.LessonRequest;
import vn.anhtuan.finalProject.dto.response.ApiResponse;
import vn.anhtuan.finalProject.dto.response.UserResponse;
import vn.anhtuan.finalProject.entity.Chapter;
import vn.anhtuan.finalProject.entity.Lesson;
import vn.anhtuan.finalProject.repository.SubjectRepository;
import vn.anhtuan.finalProject.service.IUserService;
import vn.anhtuan.finalProject.service.impl.AdminContentServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private SubjectRepository subjectRepository; // Hoặc dùng Service

    @Autowired
    private AdminContentServiceImpl adminContentService;

    @Autowired
    private IUserService userService;

    // ==================== USER MANAGEMENT ====================

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        logger.info("ADMIN API: Lấy danh sách Users");
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(users, "Lấy danh sách thành công"));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa người dùng thành công"));
    }

    @GetMapping("/subjects")
    public ResponseEntity<?> getAllSubjects() {
        // Gọi Service lấy danh sách
        return ResponseEntity.ok(ApiResponse.success(subjectRepository.findAll(), "Thành công"));
    }

    // ==================== CHAPTER (CHƯƠNG) ====================

    // 1. Lấy danh sách chương theo môn (Quan trọng cho trang ChapterList)
    @GetMapping("/subjects/{subjectId}/chapters")
    public ResponseEntity<ApiResponse<List<Chapter>>> getChaptersBySubject(@PathVariable Integer subjectId) {
        List<Chapter> chapters = adminContentService.getChaptersBySubject(subjectId);
        return ResponseEntity.ok(ApiResponse.success(chapters, "Lấy danh sách chương thành công"));
    }

    // 2. Lấy chi tiết chương (Để fill form Edit)
    @GetMapping("/chapters/{id}")
    public ResponseEntity<ApiResponse<Chapter>> getChapterById(@PathVariable Long id) {
        Chapter chapter = adminContentService.getChapterById(id);
        return ResponseEntity.ok(ApiResponse.success(chapter, "Lấy chi tiết chương thành công"));
    }

    @PostMapping("/chapters")
    public ResponseEntity<?> createChapter(@Valid @RequestBody ChapterRequest request) {
        var chapter = adminContentService.createChapter(request);
        return ResponseEntity.ok(ApiResponse.success(chapter, "Tạo chương thành công"));
    }

    @PutMapping("/chapters/{id}")
    public ResponseEntity<?> updateChapter(@PathVariable Long id, @Valid @RequestBody ChapterRequest request) {
        var chapter = adminContentService.updateChapter(id, request);
        return ResponseEntity.ok(ApiResponse.success(chapter, "Cập nhật chương thành công"));
    }

    @DeleteMapping("/chapters/{id}")
    public ResponseEntity<?> deleteChapter(@PathVariable Long id) {
        adminContentService.deleteChapter(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa chương thành công"));
    }

    // ==================== LESSON (BÀI HỌC) ====================

    // 6. Lấy danh sách bài học trong chương (Quan trọng cho trang LessonList)
    @GetMapping("/chapters/{chapterId}/lessons")
    public ResponseEntity<ApiResponse<List<Lesson>>> getLessonsByChapter(@PathVariable Long chapterId) {
        List<Lesson> lessons = adminContentService.getLessonsByChapterId(chapterId);
        return ResponseEntity.ok(ApiResponse.success(lessons, "Lấy danh sách bài học thành công"));
    }

    // 7. Lấy chi tiết bài học (Quan trọng để lấy nội dung LaTeX sửa lại)
    @GetMapping("/lessons/{id}")
    public ResponseEntity<ApiResponse<Lesson>> getLessonById(@PathVariable Long id) {
        Lesson lesson = adminContentService.getLessonById(id);
        return ResponseEntity.ok(ApiResponse.success(lesson, "Lấy chi tiết bài học thành công"));
    }

    @PostMapping("/lessons")
    public ResponseEntity<?> createLesson(@Valid @RequestBody LessonRequest request) {
        var lesson = adminContentService.createLesson(request);
        return ResponseEntity.ok(ApiResponse.success(lesson, "Tạo bài học thành công"));
    }

    @PutMapping("/lessons/{id}")
    public ResponseEntity<?> updateLesson(@PathVariable Long id, @Valid @RequestBody LessonRequest request) {
        var lesson = adminContentService.updateLesson(id, request);
        return ResponseEntity.ok(ApiResponse.success(lesson, "Cập nhật bài học thành công"));
    }

    @DeleteMapping("/lessons/{id}")
    public ResponseEntity<?> deleteLesson(@PathVariable Long id) {
        adminContentService.deleteLesson(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa bài học thành công"));
    }
}