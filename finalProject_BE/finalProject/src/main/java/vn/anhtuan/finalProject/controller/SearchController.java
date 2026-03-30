package vn.anhtuan.finalProject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.anhtuan.finalProject.dto.response.ApiResponse;
import vn.anhtuan.finalProject.dto.response.SearchItemDto;
import vn.anhtuan.finalProject.entity.Document;
import vn.anhtuan.finalProject.entity.Lesson;
import vn.anhtuan.finalProject.entity.Quiz;
import vn.anhtuan.finalProject.repository.DocumentRepository;
import vn.anhtuan.finalProject.repository.LessonRepository;
import vn.anhtuan.finalProject.repository.QuizRepository;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/student/search")
public class SearchController {

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SearchItemDto>>> searchAll(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "grade", required = false) Integer grade) {

        List<SearchItemDto> results = new ArrayList<>();

        if (keyword == null || keyword.trim().isEmpty() || grade == null) {
            // Đã fix: Thêm status code 200 vào tham số đầu tiên
            return ResponseEntity.ok(new ApiResponse<>(200, true, "No keyword or grade provided", results));
        }

        // 1. Search Lessons
        List<Lesson> lessons = lessonRepository.searchByKeywordAndGrade(keyword, grade);
        for (Lesson l : lessons) {
            // Sử dụng từ khóa new thay vì builder
            results.add(new SearchItemDto(
                    l.getId(),
                    l.getTitleVi(),
                    l.getTitleEn(),
                    "LESSON",
                    l.getChapter().getSubject().getCode(),
                    l.getChapter().getId()
            ));
        }

        // 2. Search Quizzes
        List<Quiz> quizzes = quizRepository.searchByKeywordAndGrade(keyword, grade);
        for (Quiz q : quizzes) {
            // Sử dụng từ khóa new thay vì builder
            results.add(new SearchItemDto(
                    q.getId().longValue(),
                    q.getTitleVi(),
                    q.getTitleEn(),
                    "QUIZ",
                    q.getChapter().getSubject().getCode(),
                    q.getChapter().getId()
            ));
        }

        // 3. Search Documents
        List<Document> docs = documentRepository.searchByKeywordAndGrade(keyword, grade);
        for (Document d : docs) {
            // Sử dụng từ khóa new thay vì builder (truyền null cho chapterId vì document không có)
            results.add(new SearchItemDto(
                    d.getId(),
                    d.getTitleVi(),
                    d.getTitleEn(),
                    "DOCUMENT",
                    d.getSubjectCode(),
                    null
            ));
        }

        // Đã fix: Thêm status code 200 vào tham số đầu tiên
        return ResponseEntity.ok(new ApiResponse<>(200, true, "Search successful", results));
    }
}