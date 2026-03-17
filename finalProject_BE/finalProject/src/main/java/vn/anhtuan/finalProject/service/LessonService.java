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

        return lessons.stream().map(lesson -> {
            LessonResponse response = new LessonResponse();
            response.setId(lesson.getId());
            response.setChapterId(lesson.getChapter().getId());
            response.setOrderNo(lesson.getOrderNo());
            response.setVideoUrl(lesson.getVideoUrl());

            // Lọc Title
            String title = QuizResponse.getLocalizedText(lesson.getTitleVi(), lesson.getTitleEn(), lang);
            response.setTitle(title);

            // Map danh sách Contents
            if (lesson.getContents() != null) {
                List<LessonContentResponse> contentResponses = lesson.getContents().stream().map(content -> {
                    LessonContentResponse cRes = new LessonContentResponse();
                    cRes.setId(content.getId());
                    cRes.setContentType(content.getContentType().name());
                    cRes.setContentOrder(content.getContentOrder());

                    // Lọc Content Value
                    String value = QuizResponse.getLocalizedText(content.getContentValueVi(), content.getContentValueEn(), lang);
                    cRes.setContentValue(value);

                    return cRes;
                }).collect(Collectors.toList());
                response.setContents(contentResponses);
            }

            // Nếu có include Bài tập tự luyện, map Exercises ở đây (Tùy logic Controller của bạn)

            return response;
        }).collect(Collectors.toList());
    }
}