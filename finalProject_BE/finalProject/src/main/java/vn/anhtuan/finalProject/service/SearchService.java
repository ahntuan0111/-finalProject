package vn.anhtuan.finalProject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.anhtuan.finalProject.dto.response.SearchItemDto;
import vn.anhtuan.finalProject.entity.Document;
import vn.anhtuan.finalProject.entity.Lesson;
import vn.anhtuan.finalProject.entity.Quiz;
import vn.anhtuan.finalProject.repository.DocumentRepository;
import vn.anhtuan.finalProject.repository.LessonRepository;
import vn.anhtuan.finalProject.repository.QuizRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Service layer cho Search (Tìm kiếm toàn cục).
 * Tập trung toàn bộ logic query + mapping DTO tại đây.
 * SearchController chỉ được gọi Service này.
 */
@Service
@Transactional(readOnly = true)
public class SearchService {

    @Autowired private LessonRepository lessonRepository;
    @Autowired private QuizRepository quizRepository;
    @Autowired private DocumentRepository documentRepository;

    /**
     * Tìm kiếm tất cả: Lesson, Quiz, Document theo keyword và grade.
     *
     * @return List<SearchItemDto> — không bao giờ trả entity ra ngoài
     */
    public List<SearchItemDto> searchAll(String keyword, Integer grade) {
        List<SearchItemDto> results = new ArrayList<>();

        // 1. Search Lessons
        List<Lesson> lessons = lessonRepository.searchByKeywordAndGrade(keyword, grade);
        for (Lesson l : lessons) {
            results.add(new SearchItemDto(
                    l.getId(),
                    l.getTitleVi(),
                    l.getTitleEn(),
                    "LESSON",
                    l.getChapter().getSubject().getCode(),
                    l.getChapter().getId()
            ));
        }

        // 2. Search Quizzes
        List<Quiz> quizzes = quizRepository.searchByKeywordAndGrade(keyword, grade);
        for (Quiz q : quizzes) {
            results.add(new SearchItemDto(
                    q.getId().longValue(),
                    q.getTitleVi(),
                    q.getTitleEn(),
                    "QUIZ",
                    q.getChapter().getSubject().getCode(),
                    q.getChapter().getId()
            ));
        }

        // 3. Search Documents
        List<Document> docs = documentRepository.searchByKeywordAndGrade(keyword, grade);
        for (Document d : docs) {
            results.add(new SearchItemDto(
                    d.getId(),
                    d.getTitleVi(),
                    d.getTitleEn(),
                    "DOCUMENT",
                    d.getSubjectCode(),
                    null // Document không thuộc chapter cụ thể
            ));
        }

        return results;
    }
}
