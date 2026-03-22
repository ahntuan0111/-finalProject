package vn.anhtuan.finalProject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.anhtuan.finalProject.dto.request.ExerciseRequest;
import vn.anhtuan.finalProject.dto.request.SolutionRequest;
import vn.anhtuan.finalProject.dto.response.ExerciseResponse;
import vn.anhtuan.finalProject.dto.response.ExerciseSolutionResponse;
import vn.anhtuan.finalProject.dto.response.QuizResponse; // Import Helper class
import vn.anhtuan.finalProject.entity.*;
import vn.anhtuan.finalProject.exception.ResourceNotFoundException;
import vn.anhtuan.finalProject.repository.ExerciseRepository;
import vn.anhtuan.finalProject.repository.ExerciseSubmissionRepository;
import vn.anhtuan.finalProject.repository.LessonRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExerciseService {

    @Autowired private ExerciseRepository exerciseRepository;
    @Autowired private LessonRepository lessonRepository;
    @Autowired private GeminiValidationService geminiValidationService;
    @Autowired private IUserService userService;
    @Autowired private ExerciseSubmissionRepository submissionRepository;

    // ========================================================================
    // 1. API CHO MOBILE APP (Trả về DTO đã lọc 1 ngôn ngữ)
    // ========================================================================
    public List<ExerciseResponse> getExercisesByLessonForMobile(Long lessonId, String lang) {
        List<Exercise> exercises = exerciseRepository.findByLessonIdOrderByOrderNoAsc(lessonId);

        return exercises.stream().map(exercise -> {
            ExerciseResponse response = new ExerciseResponse();
            response.setId(exercise.getId());
            response.setQuestionType(exercise.getQuestionType().name());

            // Sử dụng chung hàm Helper an toàn từ QuizResponse
            String questionValue = QuizResponse.getLocalizedText(exercise.getQuestionValueVi(), exercise.getQuestionValueEn(), lang);
            response.setQuestionValue(questionValue);

            // Gắn 3 Gợi ý học tập
            response.setHint1(QuizResponse.getLocalizedText(exercise.getHint1Vi(), exercise.getHint1En(), lang));
            response.setHint2(QuizResponse.getLocalizedText(exercise.getHint2Vi(), exercise.getHint2En(), lang));
            response.setHint3(QuizResponse.getLocalizedText(exercise.getHint3Vi(), exercise.getHint3En(), lang));

            response.setOrderNo(exercise.getOrderNo());

            if (exercise.getSolutions() != null) {
                List<ExerciseSolutionResponse> solutionResponses = exercise.getSolutions().stream().map(sol -> {
                    ExerciseSolutionResponse solRes = new ExerciseSolutionResponse();
                    solRes.setId(sol.getId());
                    solRes.setSolutionType(sol.getContentType().name());

                    String solutionValue = QuizResponse.getLocalizedText(sol.getSolutionValueVi(), sol.getSolutionValueEn(), lang);
                    solRes.setSolutionValue(solutionValue);

                    solRes.setSolutionOrder(sol.getSolutionOrder());
                    return solRes;
                }).collect(Collectors.toList());
                response.setSolutions(solutionResponses); // Set đúng List DTO
            }
            return response;
        }).collect(Collectors.toList());
    }

    // ========================================================================
    // 2. AI VALIDATION LÀM BÀI TRÊN MOBILE
    // ========================================================================
    
    public GeminiValidationService.GeminiValidationResult validateImage(Long exerciseId, String base64Image, String lang) {
        Exercise exercise = getExerciseById(exerciseId);
        
        // Pass the question text to Gemini for context
        String questionContent = "vi".equalsIgnoreCase(lang) ? exercise.getQuestionValueVi() : exercise.getQuestionValueEn();
        return geminiValidationService.validateHandwriting(base64Image, questionContent, lang);
    }
    
    @Transactional
    public vn.anhtuan.finalProject.dto.response.ExerciseSubmissionResponse saveSubmission(Long exerciseId, vn.anhtuan.finalProject.dto.request.SaveSubmissionRequest request) {
        User user = userService.getCurrentUser();
        Exercise exercise = getExerciseById(exerciseId);
        
        ExerciseSubmission submission = new ExerciseSubmission();
        submission.setUser(user);
        submission.setExercise(exercise);
        submission.setImageUrl(request.getFirebaseImageUrl());
        submission.setIsValid(true);
        submission.setAiReason(request.getAiReason());
        
        submissionRepository.save(submission);
        
        return vn.anhtuan.finalProject.dto.response.ExerciseSubmissionResponse.builder()
                .id(submission.getId())
                .userId(user.getId())
                .exerciseId(exercise.getId())
                .imageUrl(submission.getImageUrl())
                .isValid(submission.getIsValid())
                .aiReason(submission.getAiReason())
                .submittedAt(submission.getSubmittedAt())
                .build();
    }

    // ========================================================================
    // 2. API CHO ADMIN (CRUD trực tiếp với Entity)
    // ========================================================================

    public List<Exercise> getExercisesByLesson(Long lessonId) {
        return exerciseRepository.findByLessonIdOrderByOrderNoAsc(lessonId);
    }

    public Exercise getExerciseById(Long id) {
        return exerciseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài tập ID: " + id));
    }

    @Transactional
    public Exercise createExercise(ExerciseRequest request) {
        Lesson lesson = lessonRepository.findById(request.getLessonId())
                .orElseThrow(() -> new ResourceNotFoundException("Bài học không tồn tại"));

        Exercise exercise = new Exercise();
        exercise.setQuestionType(request.getQuestionType());
        exercise.setQuestionValueVi(request.getQuestionValueVi());
        exercise.setQuestionValueEn(request.getQuestionValueEn());
        
        exercise.setHint1Vi(request.getHint1Vi());
        exercise.setHint1En(request.getHint1En());
        exercise.setHint2Vi(request.getHint2Vi());
        exercise.setHint2En(request.getHint2En());
        exercise.setHint3Vi(request.getHint3Vi());
        exercise.setHint3En(request.getHint3En());
        
        exercise.setOrderNo(request.getOrderNo());
        exercise.setLesson(lesson);

        List<ExerciseSolution> solutions = new ArrayList<>();
        if (request.getSolutions() != null) {
            for (SolutionRequest solReq : request.getSolutions()) {
                ExerciseSolution sol = new ExerciseSolution();

                sol.setContentType(solReq.getType());
                sol.setSolutionValueVi(solReq.getValueVi());
                sol.setSolutionValueEn(solReq.getValueEn());
                sol.setSolutionOrder(solReq.getOrder());
                sol.setExercise(exercise);

                solutions.add(sol);
            }
        }
        exercise.setSolutions(solutions);

        return exerciseRepository.save(exercise);
    }

    @Transactional
    public Exercise updateExercise(Long id, ExerciseRequest request) {
        Exercise exercise = getExerciseById(id);

        exercise.setQuestionType(request.getQuestionType());
        exercise.setQuestionValueVi(request.getQuestionValueVi());
        exercise.setQuestionValueEn(request.getQuestionValueEn());
        
        exercise.setHint1Vi(request.getHint1Vi());
        exercise.setHint1En(request.getHint1En());
        exercise.setHint2Vi(request.getHint2Vi());
        exercise.setHint2En(request.getHint2En());
        exercise.setHint3Vi(request.getHint3Vi());
        exercise.setHint3En(request.getHint3En());
        
        exercise.setOrderNo(request.getOrderNo());

        if (exercise.getSolutions() != null) {
            exercise.getSolutions().clear();
        } else {
            exercise.setSolutions(new ArrayList<>());
        }

        if (request.getSolutions() != null) {
            for (SolutionRequest solReq : request.getSolutions()) {
                ExerciseSolution sol = new ExerciseSolution();

                sol.setContentType(solReq.getType());
                sol.setSolutionValueVi(solReq.getValueVi());
                sol.setSolutionValueEn(solReq.getValueEn());
                sol.setSolutionOrder(solReq.getOrder());
                sol.setExercise(exercise);

                exercise.getSolutions().add(sol);
            }
        }

        return exerciseRepository.save(exercise);
    }

    @Transactional
    public void deleteExercise(Long id) {
        if (!exerciseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy bài tập để xóa");
        }
        exerciseRepository.deleteById(id);
    }
}