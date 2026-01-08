package vn.anhtuan.finalProject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.anhtuan.finalProject.entity.Chapter;
import vn.anhtuan.finalProject.repository.ChapterRepository;

import java.util.List;

@Service
public class ChapterService {

    @Autowired
    private ChapterRepository chapterRepository;

    public List<Chapter> getChaptersBySubject(Integer subjectId) {
        return chapterRepository.findBySubjectIdOrderByOrderNoAsc(subjectId);
    }

    // Các hàm CRUD khác (Create, Update, Delete) nếu cần...
}