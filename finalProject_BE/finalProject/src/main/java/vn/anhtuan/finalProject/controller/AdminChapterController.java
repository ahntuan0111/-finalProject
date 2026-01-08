package vn.anhtuan.finalProject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.anhtuan.finalProject.dto.response.ApiResponse; // Giả sử bạn có class này
import vn.anhtuan.finalProject.entity.Chapter;
import vn.anhtuan.finalProject.service.ChapterService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/chapters") // Prefix chung
public class AdminChapterController {

    @Autowired
    private ChapterService chapterService;

    // API: GET /api/admin/chapters/by-subject/{subjectId}
    @GetMapping("/by-subject/{subjectId}")
    public ResponseEntity<?> getChaptersBySubject(@PathVariable Integer subjectId) {
        List<Chapter> chapters = chapterService.getChaptersBySubject(subjectId);

        // Nếu bạn dùng class ApiResponse bọc dữ liệu:
        return ResponseEntity.ok(ApiResponse.success(chapters, "Lấy danh sách chương thành công"));

        // Hoặc trả về list trực tiếp nếu chưa có ApiResponse:
        // return ResponseEntity.ok(chapters);
    }
}