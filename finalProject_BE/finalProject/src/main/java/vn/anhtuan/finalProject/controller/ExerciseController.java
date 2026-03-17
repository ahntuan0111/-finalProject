package vn.anhtuan.finalProject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.anhtuan.finalProject.dto.response.ApiResponse;
import vn.anhtuan.finalProject.dto.response.ExerciseResponse;
import vn.anhtuan.finalProject.service.ExerciseService;

import java.util.List;

@RestController
@RequestMapping("/api/exercises")
public class ExerciseController {

    @Autowired
    private ExerciseService exerciseService;

    // API lấy danh sách bài tập kèm lời giải theo LessonID
    @GetMapping("/by-lesson/{lessonId}")
    public ResponseEntity<?> getExercisesByLesson(
            @PathVariable Long lessonId,
            @RequestHeader(name = "Accept-Language", defaultValue = "vi") String lang // Bổ sung Header ngôn ngữ
    ) {
        // ĐÃ SỬA: Gọi hàm ForMobile để trả về DTO đã lọc ngôn ngữ
        List<ExerciseResponse> exercises = exerciseService.getExercisesByLessonForMobile(lessonId, lang);

        return ResponseEntity.ok(ApiResponse.success(exercises, "Lấy danh sách bài tập thành công"));
    }
}