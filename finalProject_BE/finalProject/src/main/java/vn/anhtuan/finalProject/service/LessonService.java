package vn.anhtuan.finalProject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.anhtuan.finalProject.entity.Lesson;
import vn.anhtuan.finalProject.repository.LessonRepository;

import java.util.List;

@Service
public class LessonService {

    @Autowired
    private LessonRepository lessonRepository;

    // Lấy danh sách bài học theo Chapter ID, sắp xếp theo thứ tự
    public List<Lesson> getLessonsByChapter(Long chapterId) {
        return lessonRepository.findByChapterIdOrderByOrderNoAsc(chapterId);
    }

    // ... Các hàm CRUD khác cho Lesson (Create, Update, Delete) nếu bạn đã làm
}