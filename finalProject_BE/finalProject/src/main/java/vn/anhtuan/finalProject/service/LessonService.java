package vn.anhtuan.finalProject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.anhtuan.finalProject.dto.response.LessonContentResponse;
import vn.anhtuan.finalProject.dto.response.LessonResponse;
import vn.anhtuan.finalProject.dto.response.QuizResponse; // Helper
import vn.anhtuan.finalProject.entity.Lesson;
import vn.anhtuan.finalProject.repository.LessonRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LessonService {

    @Autowired
    private LessonRepository lessonRepository;

    // Cho Admin: Lấy thẳng Entity
    public List<Lesson> getLessonsByChapter(Long chapterId) {
        return lessonRepository.findByChapterIdOrderByOrderNoAsc(chapterId);
    }

    // THÊM MỚI: Dành riêng cho Mobile, trả về Response DTO đã lọc ngôn ngữ
    public List<LessonResponse> getLessonsForMobile(Long chapterId, String lang) {
        List<Lesson> lessons = lessonRepository.findByChapterIdOrderByOrderNoAsc(chapterId);
        return lessons.stream().map(l -> mapToResponse(l, lang)).collect(Collectors.toList());
    }

    public LessonResponse getLessonByIdForMobile(Long id, String lang) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new vn.anhtuan.finalProject.exception.ResourceNotFoundException("Bài học không tồn tại"));
        return mapToResponse(lesson, lang);
    }

    private LessonResponse mapToResponse(Lesson lesson, String lang) {
        LessonResponse response = new LessonResponse();
        response.setId(lesson.getId());
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
        return response;
    }
}