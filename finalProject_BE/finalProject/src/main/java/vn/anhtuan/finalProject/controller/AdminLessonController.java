package vn.anhtuan.finalProject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.anhtuan.finalProject.dto.response.ApiResponse;
import vn.anhtuan.finalProject.entity.Lesson;
import vn.anhtuan.finalProject.service.LessonService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/lessons") // Prefix chung cho các API bài học
public class AdminLessonController {

    @Autowired
    private LessonService lessonService;

    // API: Lấy danh sách bài học theo chương
    // URL: /api/admin/lessons/by-chapter/{chapterId}
    @GetMapping("/by-chapter/{chapterId}")
    public ResponseEntity<?> getLessonsByChapter(@PathVariable Long chapterId) {
        List<Lesson> lessons = lessonService.getLessonsByChapter(chapterId);
        return ResponseEntity.ok(ApiResponse.success(lessons, "Lấy danh sách bài học thành công"));
    }
}