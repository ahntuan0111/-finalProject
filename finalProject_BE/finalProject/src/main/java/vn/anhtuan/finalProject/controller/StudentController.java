package vn.anhtuan.finalProject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.anhtuan.finalProject.dto.response.ApiResponse;
import vn.anhtuan.finalProject.repository.ChapterRepository;
import vn.anhtuan.finalProject.repository.LessonRepository;
import vn.anhtuan.finalProject.exception.ResourceNotFoundException;

@RestController
@RequestMapping("/api/student") // Endpoint cho học sinh/mobile
public class StudentController {

    @Autowired private ChapterRepository chapterRepository;
    @Autowired private LessonRepository lessonRepository;

    // Lấy danh sách chương theo môn học (Toán 6, Toán 7...)
    @GetMapping("/subjects/{subjectId}/chapters")
    public ResponseEntity<?> getChaptersBySubject(@PathVariable Integer subjectId) {
        var chapters = chapterRepository.findBySubjectIdOrderByOrderNoAsc(subjectId);
        return ResponseEntity.ok(ApiResponse.success(chapters, "Thành công"));
    }

    // Lấy chi tiết bài học (Use-case 2: Mobile gọi API này để lấy HTML/LaTeX)
    @GetMapping("/lessons/{id}")
    public ResponseEntity<?> getLessonDetail(@PathVariable Long id) {
        var lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bài học không tồn tại"));

        // Spring Boot sẽ tự động serialize object Lesson và list contents thành JSON
        // Chuỗi LaTeX trong contentValue sẽ được giữ nguyên, ví dụ: "$$ S = \\pi r^2 $$"
        return ResponseEntity.ok(ApiResponse.success(lesson, "Thành công"));
    }
}