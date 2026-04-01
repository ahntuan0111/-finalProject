package vn.anhtuan.finalProject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.anhtuan.finalProject.dto.response.ApiResponse;
import vn.anhtuan.finalProject.dto.response.SearchItemDto;
import vn.anhtuan.finalProject.service.SearchService;

import java.util.List;

/**
 * Controller tìm kiếm toàn cục dành cho học sinh.
 *
 * Nguyên tắc: Controller chỉ nhận Request → gọi Service → trả Response.
 * KHÔNG inject Repository trực tiếp, KHÔNG chứa logic business.
 */
@RestController
@RequestMapping("/api/student/search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    /**
     * GET /api/student/search?keyword=...&grade=7
     * Tìm kiếm Lesson, Quiz, Document theo keyword và grade.
     * Trả List<SearchItemDto> — không bao giờ trả entity thô.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<SearchItemDto>>> searchAll(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "grade", required = false) Integer grade) {

        // Validation: thiếu tham số → trả danh sách rỗng ngay, không vào service
        if (keyword == null || keyword.trim().isEmpty() || grade == null) {
            return ResponseEntity.ok(new ApiResponse<>(200, true, "No keyword or grade provided", List.of()));
        }

        List<SearchItemDto> results = searchService.searchAll(keyword.trim(), grade);
        return ResponseEntity.ok(new ApiResponse<>(200, true, "Search successful", results));
    }
}