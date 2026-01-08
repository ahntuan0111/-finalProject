package vn.anhtuan.finalProject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.anhtuan.finalProject.dto.request.ExerciseRequest;
import vn.anhtuan.finalProject.dto.request.SolutionRequest;
import vn.anhtuan.finalProject.entity.*;
import vn.anhtuan.finalProject.exception.ResourceNotFoundException;
import vn.anhtuan.finalProject.repository.ExerciseRepository;
import vn.anhtuan.finalProject.repository.LessonRepository;

import java.util.List;

@Service
public class ExerciseService {

    @Autowired private ExerciseRepository exerciseRepository;
    @Autowired private LessonRepository lessonRepository;

    // 1. Lấy danh sách bài tập theo Lesson ID
    public List<Exercise> getExercisesByLesson(Long lessonId) {
        return exerciseRepository.findByLessonIdOrderByOrderNoAsc(lessonId);
    }

    // 2. Lấy chi tiết 1 bài tập
    public Exercise getExerciseById(Long id) {
        return exerciseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài tập ID: " + id));
    }

    // 3. Tạo mới bài tập (Kèm lời giải)
    @Transactional
    public Exercise createExercise(ExerciseRequest request) {
        Lesson lesson = lessonRepository.findById(request.getLessonId())
                .orElseThrow(() -> new ResourceNotFoundException("Bài học không tồn tại"));

        Exercise exercise = new Exercise();
        // --- Đã đúng logic mới ---
        exercise.setQuestionType(request.getQuestionType());
        exercise.setQuestionValue(request.getQuestionValue());
        exercise.setOrderNo(request.getOrderNo());
        exercise.setLesson(lesson);

        // Map danh sách lời giải
        if (request.getSolutions() != null) {
            for (SolutionRequest solReq : request.getSolutions()) {
                ExerciseSolution sol = new ExerciseSolution();

                // --- SỬA LỖI TẠI ĐÂY ---
                // Cũ: sol.setContentType(solReq.getType()); -> Sai vì Entity là SolutionType
                // Mới:
                sol.setContentType(solReq.getType());

                sol.setSolutionValue(solReq.getValue());
                sol.setSolutionOrder(solReq.getOrder());
                exercise.addSolution(sol);
            }
        }

        return exerciseRepository.save(exercise);
    }

    // 4. Cập nhật bài tập
    @Transactional
    public Exercise updateExercise(Long id, ExerciseRequest request) {
        Exercise exercise = getExerciseById(id);

        // --- Đã đúng logic mới ---
        exercise.setQuestionType(request.getQuestionType());
        exercise.setQuestionValue(request.getQuestionValue());
        exercise.setOrderNo(request.getOrderNo());

        // Xóa cũ thêm mới (Hibernate orphanRemoval sẽ tự xóa DB)
        exercise.getSolutions().clear();

        if (request.getSolutions() != null) {
            for (SolutionRequest solReq : request.getSolutions()) {
                ExerciseSolution sol = new ExerciseSolution();

                // --- SỬA LỖI TẠI ĐÂY ---
                sol.setContentType(solReq.getType());

                sol.setSolutionValue(solReq.getValue());
                sol.setSolutionOrder(solReq.getOrder());
                exercise.addSolution(sol);
            }
        }

        return exerciseRepository.save(exercise);
    }

    // 5. Xóa bài tập
    @Transactional
    public void deleteExercise(Long id) {
        if (!exerciseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy bài tập để xóa");
        }
        exerciseRepository.deleteById(id);
    }
}