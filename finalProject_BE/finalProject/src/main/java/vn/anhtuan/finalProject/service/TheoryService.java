package vn.anhtuan.finalProject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.anhtuan.finalProject.dto.response.ChapterResponse;
import vn.anhtuan.finalProject.dto.response.LessonContentResponse;
import vn.anhtuan.finalProject.dto.response.LessonResponse;
import vn.anhtuan.finalProject.dto.response.QuizResponse;
import vn.anhtuan.finalProject.entity.Chapter;
import vn.anhtuan.finalProject.entity.Lesson;
import vn.anhtuan.finalProject.entity.Subject;
import vn.anhtuan.finalProject.exception.ResourceNotFoundException;
import vn.anhtuan.finalProject.repository.ChapterRepository;
import vn.anhtuan.finalProject.repository.LessonRepository;
import vn.anhtuan.finalProject.repository.SubjectRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer cho Theory (Lý thuyết) dành cho học sinh.
 * Tập trung toàn bộ logic truy vấn + mapping DTO tại đây.
 * TheoryUserController chỉ được gọi Service này.
 */
@Service
@Transactional(readOnly = true)
public class TheoryService {

    @Autowired private ChapterRepository chapterRepository;
    @Autowired private LessonRepository lessonRepository;
    @Autowired private SubjectRepository subjectRepository;

    // ========== PUBLIC API ==========

    public List<ChapterResponse> getChaptersBySubjectId(Integer subjectId, String lang) {
        List<Chapter> chapters = chapterRepository.findBySubjectIdOrderByOrderNoAsc(subjectId);
        return toChapterResponseList(chapters, lang);
    }

    public List<ChapterResponse> getChaptersByCodeAndGrade(String code, Integer grade, String lang) {
        Subject subject = subjectRepository.findByCodeAndGradeId(code, grade)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy môn học mã: " + code + " cho khối " + grade));
        List<Chapter> chapters = chapterRepository.findBySubjectIdOrderByOrderNoAsc(subject.getId());
        return toChapterResponseList(chapters, lang);
    }

    public LessonResponse getLessonDetail(Long id, String lang) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bài học không tồn tại"));
        return toLessonDetailResponse(lesson, lang);
    }

    // ========== PRIVATE MAPPERS ==========

    private List<ChapterResponse> toChapterResponseList(List<Chapter> chapters, String lang) {
        return chapters.stream()
                .map(c -> toChapterResponse(c, lang))
                .collect(Collectors.toList());
    }

    private ChapterResponse toChapterResponse(Chapter c, String lang) {
        ChapterResponse res = new ChapterResponse();
        res.setId(c.getId());
        res.setOrderNo(c.getOrderNo());
        res.setTitle(QuizResponse.getLocalizedText(c.getTitleVi(), c.getTitleEn(), lang));

        if (c.getSubject() != null) {
            res.setSubjectId(c.getSubject().getId());
        }

        if (c.getLessons() != null) {
            List<LessonResponse> lessonResponses = c.getLessons().stream()
                    .map(l -> toLessonSummaryResponse(l, lang))
                    .collect(Collectors.toList());
            res.setLessons(lessonResponses);
        }

        return res;
    }

    /**
     * Summary: chỉ id, title, orderNo — không load contents (tiết kiệm băng thông).
     */
    private LessonResponse toLessonSummaryResponse(Lesson l, String lang) {
        LessonResponse lRes = new LessonResponse();
        lRes.setId(l.getId());
        lRes.setOrderNo(l.getOrderNo());
        lRes.setTitle(QuizResponse.getLocalizedText(l.getTitleVi(), l.getTitleEn(), lang));
        return lRes;
    }

    /**
     * Detail: đầy đủ thông tin, bao gồm contents (dùng cho màn hình học bài).
     */
    private LessonResponse toLessonDetailResponse(Lesson lesson, String lang) {
        LessonResponse response = new LessonResponse();
        response.setId(lesson.getId());
        response.setOrderNo(lesson.getOrderNo());
        response.setVideoUrl(lesson.getVideoUrl());
        response.setTitle(QuizResponse.getLocalizedText(lesson.getTitleVi(), lesson.getTitleEn(), lang));

        if (lesson.getChapter() != null) {
            response.setChapterId(lesson.getChapter().getId());
        }

        if (lesson.getContents() != null) {
            List<LessonContentResponse> contents = lesson.getContents().stream()
                    .map(c -> toLessonContentResponse(c, lang))
                    .collect(Collectors.toList());
            response.setContents(contents);
        }

        return response;
    }

    private LessonContentResponse toLessonContentResponse(
            vn.anhtuan.finalProject.entity.LessonContent c, String lang) {
        LessonContentResponse cRes = new LessonContentResponse();
        cRes.setId(c.getId());
        cRes.setContentType(c.getContentType().name());
        cRes.setContentOrder(c.getContentOrder());
        cRes.setContentValue(QuizResponse.getLocalizedText(c.getContentValueVi(), c.getContentValueEn(), lang));
        return cRes;
    }
}
