package vn.anhtuan.finalProject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.anhtuan.finalProject.dto.response.ApiResponse;
import vn.anhtuan.finalProject.entity.Chapter;
import vn.anhtuan.finalProject.entity.Subject;
import vn.anhtuan.finalProject.repository.ChapterRepository;
import vn.anhtuan.finalProject.repository.LessonRepository;
import vn.anhtuan.finalProject.exception.ResourceNotFoundException;
import vn.anhtuan.finalProject.repository.SubjectRepository;

import java.util.List;

@RestController
@RequestMapping("/api/student") // Endpoint cho học sinh/mobile
public class TheoryUserController {

    @Autowired private ChapterRepository chapterRepository;
    @Autowired private LessonRepository lessonRepository;
    @Autowired private SubjectRepository subjectRepository;

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

    // API MỚI: Lấy danh sách chương theo Code và Grade
    // GET /api/student/chapters?code=toan&grade=7
    @GetMapping("/chapters")
    public ResponseEntity<?> getChaptersByCodeAndGrade(
            @RequestParam String code,  // "toan" hoặc "khtn"
            @RequestParam Integer grade // 6, 7, 8, 9
    ) {
        // 1. Tìm ID môn học tương ứng (Ví dụ: toan + 7 -> ID = 3)
        Subject subject = subjectRepository.findByCodeAndGradeId(code, grade)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy môn học mã: " + code + " cho khối " + grade));

        // 2. Lấy danh sách chương của môn học đó
        List<Chapter> chapters = chapterRepository.findBySubjectIdOrderByOrderNoAsc(subject.getId());

        return ResponseEntity.ok(ApiResponse.success(chapters, "Thành công"));
    }
}