package vn.anhtuan.finalProject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.anhtuan.finalProject.dto.response.ApiResponse;
import vn.anhtuan.finalProject.entity.Exercise;
import vn.anhtuan.finalProject.service.ExerciseService;

import java.util.List;

@RestController
@RequestMapping("/api/exercises") // Endpoint chung cho người dùng (Học sinh)
public class ExerciseController {

    @Autowired
    private ExerciseService exerciseService;

    // API lấy danh sách bài tập kèm lời giải theo LessonID
    // GET: /api/exercises/by-lesson/{lessonId}
    @GetMapping("/by-lesson/{lessonId}")
    public ResponseEntity<?> getExercisesByLesson(@PathVariable Long lessonId) {
        List<Exercise> exercises = exerciseService.getExercisesByLesson(lessonId);

        // Lưu ý: Mặc định JPA/Hibernate sẽ fetch Lazy list solutions.
        // Khi trả về JSON, Jackson sẽ serialize list solutions này.
        // Đảm bảo trong Entity Exercise KHÔNG để @JsonIgnore trên field 'solutions'.

        return ResponseEntity.ok(ApiResponse.success(exercises, "Lấy danh sách bài tập thành công"));
    }
}