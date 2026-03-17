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
import vn.anhtuan.finalProject.dto.response.admin.ChapterAdminResponse;
import vn.anhtuan.finalProject.dto.response.admin.LessonAdminResponse;
import vn.anhtuan.finalProject.dto.response.admin.SubjectAdminResponse;
import vn.anhtuan.finalProject.entity.Chapter;
import vn.anhtuan.finalProject.entity.Lesson;
import vn.anhtuan.finalProject.service.IUserService;
import vn.anhtuan.finalProject.service.impl.AdminContentServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired private AdminContentServiceImpl adminContentService;
    @Autowired private IUserService userService;

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

    // ==================== SUBJECTS (MÔN HỌC) ====================
    @GetMapping("/subjects")
    public ResponseEntity<?> getAllSubjects() {
        List<SubjectAdminResponse> res = adminContentService.getAllSubjects().stream()
                .map(SubjectAdminResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(res, "Thành công"));
    }

    // ==================== CHAPTER (CHƯƠNG) ====================
    @GetMapping("/subjects/{subjectId}/chapters")
    public ResponseEntity<?> getChaptersBySubject(@PathVariable Integer subjectId) {
        List<ChapterAdminResponse> chapters = adminContentService.getChaptersBySubject(subjectId).stream()
                .map(ChapterAdminResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(chapters, "Lấy danh sách chương thành công"));
    }

    @GetMapping("/chapters/{id}")
    public ResponseEntity<?> getChapterById(@PathVariable Long id) {
        Chapter chapter = adminContentService.getChapterById(id);
        return ResponseEntity.ok(ApiResponse.success(ChapterAdminResponse.fromEntity(chapter), "Lấy chi tiết chương thành công"));
    }

    @PostMapping("/chapters")
    public ResponseEntity<?> createChapter(@Valid @RequestBody ChapterRequest request) {
        Chapter chapter = adminContentService.createChapter(request);
        return ResponseEntity.ok(ApiResponse.success(ChapterAdminResponse.fromEntity(chapter), "Tạo chương thành công"));
    }

    @PutMapping("/chapters/{id}")
    public ResponseEntity<?> updateChapter(@PathVariable Long id, @Valid @RequestBody ChapterRequest request) {
        Chapter chapter = adminContentService.updateChapter(id, request);
        return ResponseEntity.ok(ApiResponse.success(ChapterAdminResponse.fromEntity(chapter), "Cập nhật chương thành công"));
    }

    @DeleteMapping("/chapters/{id}")
    public ResponseEntity<?> deleteChapter(@PathVariable Long id) {
        adminContentService.deleteChapter(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa chương thành công"));
    }

    // ==================== LESSON (BÀI HỌC) ====================
    @GetMapping("/chapters/{chapterId}/lessons")
    public ResponseEntity<?> getLessonsByChapter(@PathVariable Long chapterId) {
        List<LessonAdminResponse> lessons = adminContentService.getLessonsByChapterId(chapterId).stream()
                .map(LessonAdminResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(lessons, "Lấy danh sách bài học thành công"));
    }

    @GetMapping("/lessons/{id}")
    public ResponseEntity<?> getLessonById(@PathVariable Long id) {
        Lesson lesson = adminContentService.getLessonById(id);
        return ResponseEntity.ok(ApiResponse.success(LessonAdminResponse.fromEntity(lesson), "Lấy chi tiết bài học thành công"));
    }

    @PostMapping("/lessons")
    public ResponseEntity<?> createLesson(@Valid @RequestBody LessonRequest request) {
        Lesson lesson = adminContentService.createLesson(request);
        return ResponseEntity.ok(ApiResponse.success(LessonAdminResponse.fromEntity(lesson), "Tạo bài học thành công"));
    }

    @PutMapping("/lessons/{id}")
    public ResponseEntity<?> updateLesson(@PathVariable Long id, @Valid @RequestBody LessonRequest request) {
        Lesson lesson = adminContentService.updateLesson(id, request);
        return ResponseEntity.ok(ApiResponse.success(LessonAdminResponse.fromEntity(lesson), "Cập nhật bài học thành công"));
    }

    @DeleteMapping("/lessons/{id}")
    public ResponseEntity<?> deleteLesson(@PathVariable Long id) {
        adminContentService.deleteLesson(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa bài học thành công"));
    }
}