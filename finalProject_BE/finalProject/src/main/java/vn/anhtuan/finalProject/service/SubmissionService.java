package vn.anhtuan.finalProject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.anhtuan.finalProject.dto.response.ExerciseSubmissionResponse;
import vn.anhtuan.finalProject.entity.ExerciseSubmission;
import vn.anhtuan.finalProject.repository.ExerciseSubmissionRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer cho ExerciseSubmission.
 * Controller chỉ được gọi Service này — không inject Repository trực tiếp.
 */
@Service
@Transactional(readOnly = true)
public class SubmissionService {

    @Autowired
    private ExerciseSubmissionRepository submissionRepository;

    /**
     * Lấy toàn bộ bài nộp và map sang DTO.
     * Controller không bao giờ thấy entity ExerciseSubmission.
     */
    public List<ExerciseSubmissionResponse> getAllSubmissions() {
        return submissionRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // ========== PRIVATE MAPPER ==========

    /**
     * Map entity → DTO.
     * Chú ý: user.getId() và exercise.getId() được gọi ở đây trong phạm vi
     * transaction (readOnly), Hibernate sẽ không ném LazyInitializationException.
     */
    private ExerciseSubmissionResponse toDto(ExerciseSubmission e) {
        ExerciseSubmissionResponse dto = new ExerciseSubmissionResponse();
        dto.setId(e.getId());
        dto.setUserId(e.getUser() != null ? e.getUser().getId() : null);
        dto.setExerciseId(e.getExercise() != null ? e.getExercise().getId() : null);
        dto.setImageUrl(e.getImageUrl());
        dto.setValid(e.getValid());
        dto.setAiReason(e.getAiReason());
        dto.setSubmittedAt(e.getSubmittedAt());
        return dto;
    }
}
