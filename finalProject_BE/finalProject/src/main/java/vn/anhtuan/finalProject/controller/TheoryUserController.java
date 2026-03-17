package vn.anhtuan.finalProject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.anhtuan.finalProject.dto.response.ApiResponse;
import vn.anhtuan.finalProject.dto.response.ChapterResponse;
import vn.anhtuan.finalProject.dto.response.LessonContentResponse;
import vn.anhtuan.finalProject.dto.response.LessonResponse;
import vn.anhtuan.finalProject.dto.response.QuizResponse;
import vn.anhtuan.finalProject.entity.Chapter;
import vn.anhtuan.finalProject.entity.Lesson;
import vn.anhtuan.finalProject.entity.Subject;
import vn.anhtuan.finalProject.repository.ChapterRepository;
import vn.anhtuan.finalProject.repository.LessonRepository;
import vn.anhtuan.finalProject.exception.ResourceNotFoundException;
import vn.anhtuan.finalProject.repository.SubjectRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/student")
public class TheoryUserController {

    @Autowired private ChapterRepository chapterRepository;
    @Autowired private LessonRepository lessonRepository;
    @Autowired private SubjectRepository subjectRepository;

    @GetMapping("/subjects/{subjectId}/chapters")
    public ResponseEntity<?> getChaptersBySubject(
            @PathVariable Integer subjectId,
            @RequestHeader(name = "Accept-Language", defaultValue = "vi") String lang) {

        List<Chapter> chapters = chapterRepository.findBySubjectIdOrderByOrderNoAsc(subjectId);
        List<ChapterResponse> responses = mapToChapterResponses(chapters, lang);

        return ResponseEntity.ok(ApiResponse.success(responses, "Thành công"));
    }

    // Lấy chi tiết bài học
    @GetMapping("/lessons/{id}")
    public ResponseEntity<?> getLessonDetail(
            @PathVariable Long id,
            @RequestHeader(name = "Accept-Language", defaultValue = "vi") String lang) {

        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bài học không tồn tại"));

        // Map sang LessonResponse đã lọc ngôn ngữ
        LessonResponse response = new LessonResponse();
        response.setId(lesson.getId());

        // Đã sửa an toàn: getChapter() có thể null
        if (lesson.getChapter() != null) {
            response.setChapterId(lesson.getChapter().getId());
        }

        response.setOrderNo(lesson.getOrderNo());
        response.setVideoUrl(lesson.getVideoUrl());
        response.setTitle(QuizResponse.getLocalizedText(lesson.getTitleVi(), lesson.getTitleEn(), lang));

        if (lesson.getContents() != null) {
            List<LessonContentResponse> contents = lesson.getContents().stream().map(c -> {
                LessonContentResponse cRes = new LessonContentResponse();
                cRes.setId(c.getId());
                cRes.setContentType(c.getContentType().name());
                cRes.setContentOrder(c.getContentOrder());
                cRes.setContentValue(QuizResponse.getLocalizedText(c.getContentValueVi(), c.getContentValueEn(), lang));
                return cRes;
            }).collect(Collectors.toList());
            response.setContents(contents);
        }

        return ResponseEntity.ok(ApiResponse.success(response, "Thành công"));
    }

    // Lấy danh sách chương theo Code và Grade
    @GetMapping("/chapters")
    public ResponseEntity<?> getChaptersByCodeAndGrade(
            @RequestParam String code,
            @RequestParam Integer grade,
            @RequestHeader(name = "Accept-Language", defaultValue = "vi") String lang) {

        Subject subject = subjectRepository.findByCodeAndGradeId(code, grade)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy môn học mã: " + code + " cho khối " + grade));

        // LƯU Ý: Nếu repository của bạn yêu cầu truyền Subject Entity thay vì ID,
        // thì hàm này có thể là: chapterRepository.findBySubjectOrderByOrderNoAsc(subject)
        List<Chapter> chapters = chapterRepository.findBySubjectIdOrderByOrderNoAsc(subject.getId());
        List<ChapterResponse> responses = mapToChapterResponses(chapters, lang);

        return ResponseEntity.ok(ApiResponse.success(responses, "Thành công"));
    }

    // --- Hàm Helper dùng chung để map List<Chapter> sang List<ChapterResponse> ---
    private List<ChapterResponse> mapToChapterResponses(List<Chapter> chapters, String lang) {
        return chapters.stream().map(c -> {
            ChapterResponse res = new ChapterResponse();
            res.setId(c.getId());

            // ĐÃ SỬA LỖI Ở ĐÂY: Gọi c.getSubject().getId() thay vì c.getSubjectId()
            if (c.getSubject() != null) {
                res.setSubjectId(c.getSubject().getId());
            }

            res.setOrderNo(c.getOrderNo());
            res.setTitle(QuizResponse.getLocalizedText(c.getTitleVi(), c.getTitleEn(), lang));

            // Map luôn danh sách các bài học con bên trong chương
            if (c.getLessons() != null) {
                List<LessonResponse> lessonResponses = c.getLessons().stream().map(l -> {
                    LessonResponse lRes = new LessonResponse();
                    lRes.setId(l.getId());
                    lRes.setOrderNo(l.getOrderNo());
                    lRes.setTitle(QuizResponse.getLocalizedText(l.getTitleVi(), l.getTitleEn(), lang));
                    // Không cần map 'contents' ở API list để tiết kiệm băng thông
                    return lRes;
                }).collect(Collectors.toList());
                res.setLessons(lessonResponses);
            }
            return res;
        }).collect(Collectors.toList());
    }
}