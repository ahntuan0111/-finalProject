package vn.anhtuan.finalProject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.anhtuan.finalProject.dto.response.ApiResponse;
import vn.anhtuan.finalProject.dto.response.ChapterResponse;
import vn.anhtuan.finalProject.dto.response.LessonResponse;
import vn.anhtuan.finalProject.service.TheoryService;

import java.util.List;

/**
 * Controller lý thuyết dành cho học sinh (mobile).
 *
 * Nguyên tắc: Controller chỉ nhận Request → gọi Service → trả Response.
 * KHÔNG inject Repository trực tiếp, KHÔNG chứa logic mapping, KHÔNG trả entity thô.
 */
@RestController
@RequestMapping("/api/student")
public class TheoryUserController {

    @Autowired
    private TheoryService theoryService;

    /**
     * GET /api/student/subjects/{subjectId}/chapters
     * Lấy danh sách chương theo subject ID.
     */
    @GetMapping("/subjects/{subjectId}/chapters")
    public ResponseEntity<?> getChaptersBySubject(
            @PathVariable Integer subjectId,
            @RequestHeader(name = "Accept-Language", defaultValue = "vi") String lang) {

        List<ChapterResponse> responses = theoryService.getChaptersBySubjectId(subjectId, lang);
        return ResponseEntity.ok(ApiResponse.success(responses, "Thành công"));
    }

    /**
     * GET /api/student/lessons/{id}
     * Lấy chi tiết bài học (bao gồm contents).
     */
    @GetMapping("/lessons/{id}")
    public ResponseEntity<?> getLessonDetail(
            @PathVariable Long id,
            @RequestHeader(name = "Accept-Language", defaultValue = "vi") String lang) {

        LessonResponse response = theoryService.getLessonDetail(id, lang);
        return ResponseEntity.ok(ApiResponse.success(response, "Thành công"));
    }

    /**
     * GET /api/student/chapters?code=TOAN&grade=7
     * Lấy danh sách chương theo mã môn và khối lớp.
     */
    @GetMapping("/chapters")
    public ResponseEntity<?> getChaptersByCodeAndGrade(
            @RequestParam String code,
            @RequestParam Integer grade,
            @RequestHeader(name = "Accept-Language", defaultValue = "vi") String lang) {

        List<ChapterResponse> responses = theoryService.getChaptersByCodeAndGrade(code, grade, lang);
        return ResponseEntity.ok(ApiResponse.success(responses, "Thành công"));
    }
}