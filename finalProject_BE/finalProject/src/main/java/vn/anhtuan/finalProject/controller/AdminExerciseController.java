package vn.anhtuan.finalProject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.anhtuan.finalProject.dto.request.ExerciseRequest;
import vn.anhtuan.finalProject.dto.response.ApiResponse;
import vn.anhtuan.finalProject.entity.Exercise;
import vn.anhtuan.finalProject.service.ExerciseService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/exercises")
public class AdminExerciseController {

    @Autowired private ExerciseService exerciseService;

    // Lấy danh sách bài tập của một bài học
    @GetMapping("/by-lesson/{lessonId}")
    public ResponseEntity<?> getExercisesByLesson(@PathVariable Long lessonId) {
        List<Exercise> exercises = exerciseService.getExercisesByLesson(lessonId);
        return ResponseEntity.ok(ApiResponse.success(exercises, "Lấy danh sách thành công"));
    }

    // Lấy chi tiết 1 bài tập
    @GetMapping("/{id}")
    public ResponseEntity<?> getExerciseDetail(@PathVariable Long id) {
        Exercise exercise = exerciseService.getExerciseById(id);
        return ResponseEntity.ok(ApiResponse.success(exercise, "Lấy chi tiết thành công"));
    }

    // Tạo mới
    @PostMapping
    public ResponseEntity<?> createExercise(@RequestBody ExerciseRequest request) {
        Exercise newExercise = exerciseService.createExercise(request);
        return ResponseEntity.ok(ApiResponse.success(newExercise, "Tạo bài tập thành công"));
    }

    // Cập nhật
    @PutMapping("/{id}")
    public ResponseEntity<?> updateExercise(@PathVariable Long id, @RequestBody ExerciseRequest request) {
        Exercise updatedExercise = exerciseService.updateExercise(id, request);
        return ResponseEntity.ok(ApiResponse.success(updatedExercise, "Cập nhật thành công"));
    }

    // Xóa
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExercise(@PathVariable Long id) {
        exerciseService.deleteExercise(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa thành công"));
    }
}