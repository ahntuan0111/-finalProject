package vn.anhtuan.finalProject.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.anhtuan.finalProject.dto.request.LessonCompletionRequest;
import vn.anhtuan.finalProject.dto.response.ProgressResponse;
import vn.anhtuan.finalProject.dto.response.SubjectOverviewResponse;
import vn.anhtuan.finalProject.entity.*;

import vn.anhtuan.finalProject.exception.ResourceNotFoundException;
import vn.anhtuan.finalProject.repository.*;
import vn.anhtuan.finalProject.service.ILessonProgressService;
import vn.anhtuan.finalProject.service.IUserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LessonProgressServiceImpl implements ILessonProgressService {

    private final LessonCompletionRepository lessonCompletionRepository;
    private final ProgressRepository progressRepository;
    private final LessonRepository lessonRepository;
    private final IUserService userService;

    public LessonProgressServiceImpl(LessonCompletionRepository lessonCompletionRepository, ProgressRepository progressRepository, LessonRepository lessonRepository, IUserService userService) {
        this.lessonCompletionRepository = lessonCompletionRepository;
        this.progressRepository = progressRepository;
        this.lessonRepository = lessonRepository;
        this.userService = userService;
    }

    @Override
    @Transactional
    public void markAsCompleted(LessonCompletionRequest request) {
        User user = userService.getCurrentUser();
        Lesson lesson = lessonRepository.findById(request.getLessonId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài học"));

        // 1. Tìm bản ghi LessonCompletion, nếu chưa có thì tạo mới (không dùng builder)
        LessonCompletion completion = lessonCompletionRepository.findByUserIdAndLessonId(user.getId(), lesson.getId())
                .orElseGet(() -> {
                    LessonCompletion newCompletion = new LessonCompletion();
                    newCompletion.setUser(user);
                    newCompletion.setLesson(lesson);
                    newCompletion.setTheoryCompleted(false);
                    newCompletion.setExerciseCompleted(false);
                    newCompletion.setCompletedAt(null);
                    return newCompletion;
                });

        // 2. Cập nhật trạng thái theo nút bấm (Lý thuyết hoặc Bài tập)
        if (request.getType() == CompletionType.THEORY) {
            completion.setTheoryCompleted(true);
        } else if (request.getType() == CompletionType.EXERCISE) {
            completion.setExerciseCompleted(true);
        }

        // 3. Kiểm tra xem đã xong HOÀN TOÀN bài học này chưa (Cả Lý thuyết và Bài tập đều true)
        boolean isNewlyCompleted = false;
        if (completion.isTheoryCompleted() && completion.isExerciseCompleted() && completion.getCompletedAt() == null) {
            completion.setCompletedAt(LocalDateTime.now());
            isNewlyCompleted = true; // Đánh dấu đây là lần đầu tiên hoàn thành full bài học này
        }

        lessonCompletionRepository.save(completion);

        // 4. Nếu vừa hoàn thành xong 1 bài (cả lý thuyết và bài tập), Cập nhật bảng Progress tổng
        if (isNewlyCompleted) {
            updateSubjectProgress(user, lesson.getChapter().getSubject());
        }

        // =========================================================================
        // 5. TRIGGER QUAN TRỌNG: Ghi nhận hoạt động ngày & Tính Chuỗi (Streak)
        // =========================================================================
        // Dù user chỉ mới hoàn thành lý thuyết hay bài tập, ta đều tính là hôm nay có học bài.
        // Việc này sẽ cập nhật cột lastActiveDate thành hôm nay -> Cron Job 8h tối sẽ bỏ qua học sinh này.
        userService.recordDailyActivity(user.getUsername());
        // =========================================================================
    }

    private void updateSubjectProgress(User user, Subject subject) {
        // Đếm tổng số bài học hiện có của môn này
        int totalLessons = lessonRepository.countByChapter_Subject_Id(subject.getId());

        // Đếm số bài học user ĐÃ HOÀN THÀNH của môn này
        int completedLessons = lessonCompletionRepository.countCompletedLessonsByUserAndSubject(user.getId(), subject.getId());

        // Tính %
        double percent = (totalLessons == 0) ? 0.0 : ((double) completedLessons / totalLessons) * 100.0;
        // Làm tròn 2 chữ số thập phân
        percent = Math.round(percent * 100.0) / 100.0;

        // Tìm bảng Progress của môn học này, nếu chưa có thì tạo mới (không dùng builder)
        Progress progress = progressRepository.findByUserIdAndSubjectId(user.getId(), subject.getId())
                .orElseGet(() -> {
                    Progress newProgress = new Progress();
                    newProgress.setUser(user);
                    newProgress.setSubject(subject);
                    newProgress.setGrade(subject.getGrade());
                    newProgress.setCompletedLessons(0);
                    newProgress.setTotalLessons(0);
                    newProgress.setProgressPercent(0.0);
                    return newProgress;
                });

        // Cập nhật số liệu tiến trình
        progress.setTotalLessons(totalLessons);
        progress.setCompletedLessons(completedLessons);
        progress.setProgressPercent(percent);
        // Lưu ý: Cột updatedAt sẽ được Hibernate tự động cập nhật nhờ @UpdateTimestamp trong Entity

        progressRepository.save(progress);
    }

    @Override
    public List<ProgressResponse> getMyProgress(Integer gradeId) {
        // Lấy thông tin user đang đăng nhập
        User user = userService.getCurrentUser();

        // Query database lấy danh sách tiến trình của user này theo khối lớp
        List<Progress> progressList = progressRepository.findByUserIdAndGradeId(user.getId(), gradeId);

        // Map từ Entity sang DTO để trả về cho Mobile
        return progressList.stream().map(p -> new ProgressResponse(
                p.getSubject().getId(),
                p.getSubject().getCode(),
                p.getGrade().getId().intValue(), // Ép kiểu về int đề phòng trường hợp id của Grade là byte/tinyint
                p.getCompletedLessons(),
                p.getTotalLessons(),
                p.getProgressPercent()
        )).collect(Collectors.toList());
    }

    @Override
    public SubjectOverviewResponse getSubjectOverview(String subjectCode, Integer gradeId) {
        User user = userService.getCurrentUser();

        // 1. Lấy % tiến độ môn học
        double progressPercent = 0.0;
        List<Progress> progressList = progressRepository.findByUserIdAndGradeId(user.getId(), gradeId);
        for (Progress p : progressList) {
            if (p.getSubject().getCode().equals(subjectCode)) {
                progressPercent = p.getProgressPercent();
                break;
            }
        }

        // 2. Tìm bài học tiếp theo
        Long nextLessonId = -1L; // Mặc định -1 nếu không có bài nào trong DB
        List<Long> uncompleted = lessonRepository.findUncompletedLessonIds(user.getId(), subjectCode, gradeId);

        if (!uncompleted.isEmpty()) {
            nextLessonId = uncompleted.get(0); // Lấy bài chưa học đầu tiên
        } else {
            // Nếu đã học XONG HẾT, lấy bài học đầu tiên của môn đó để ôn tập lại
            List<Long> allLessons = lessonRepository.findAllLessonIds(subjectCode, gradeId);
            if (!allLessons.isEmpty()) {
                nextLessonId = allLessons.get(0);
            }
        }
        return new SubjectOverviewResponse(progressPercent, nextLessonId);
    }

}