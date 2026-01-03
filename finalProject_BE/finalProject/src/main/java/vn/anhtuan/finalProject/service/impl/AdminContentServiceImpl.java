package vn.anhtuan.finalProject.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.anhtuan.finalProject.dto.request.ChapterRequest;
import vn.anhtuan.finalProject.dto.request.LessonRequest;
import vn.anhtuan.finalProject.entity.*;
import vn.anhtuan.finalProject.exception.ResourceNotFoundException;
import vn.anhtuan.finalProject.repository.*;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class AdminContentServiceImpl {

    @Autowired private SubjectRepository subjectRepository;
    @Autowired private ChapterRepository chapterRepository;
    @Autowired private LessonRepository lessonRepository;

    // ========================================================================
    // 1. QUẢN LÝ SUBJECT (READ ONLY)
    // ========================================================================

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    public Subject getSubjectById(Integer id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Môn học không tồn tại với ID: " + id));
    }

    // ========================================================================
    // 2. QUẢN LÝ CHAPTER (CRUD)
    // ========================================================================

    // CREATE
    public Chapter createChapter(ChapterRequest request) {
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Môn học không tồn tại"));

        Chapter chapter = new Chapter();
        chapter.setTitle(request.getTitle());
        chapter.setOrderNo(request.getOrderNo());
        chapter.setSubject(subject);

        return chapterRepository.save(chapter);
    }

    // READ (GET LIST)
    public List<Chapter> getChaptersBySubject(Integer subjectId) {
        return chapterRepository.findBySubjectIdOrderByOrderNoAsc(subjectId);
    }

    // READ (GET DETAIL) - Thêm mới
    public Chapter getChapterById(Long id) {
        return chapterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chương không tồn tại với ID: " + id));
    }

    // UPDATE
    public Chapter updateChapter(Long chapterId, ChapterRequest request) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new ResourceNotFoundException("Chương không tồn tại với ID: " + chapterId));

        chapter.setTitle(request.getTitle());
        chapter.setOrderNo(request.getOrderNo());

        // Nếu admin muốn chuyển chương sang môn học khác
        if (!chapter.getSubject().getId().equals(request.getSubjectId())) {
            Subject newSubject = subjectRepository.findById(request.getSubjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Môn học mới không tồn tại"));
            chapter.setSubject(newSubject);
        }

        return chapterRepository.save(chapter);
    }

    // DELETE
    public void deleteChapter(Long chapterId) {
        if (!chapterRepository.existsById(chapterId)) {
            throw new ResourceNotFoundException("Chương không tồn tại để xóa");
        }
        chapterRepository.deleteById(chapterId);
    }

    // ========================================================================
    // 3. QUẢN LÝ LESSON (CRUD - XỬ LÝ LATEX)
    // ========================================================================

    // CREATE
    public Lesson createLesson(LessonRequest request) {
        Chapter chapter = chapterRepository.findById(request.getChapterId())
                .orElseThrow(() -> new ResourceNotFoundException("Chương không tồn tại"));

        Lesson lesson = new Lesson();
        lesson.setTitle(request.getTitle());
        lesson.setVideoUrl(request.getVideoUrl());
        lesson.setOrderNo(request.getOrderNo());
        lesson.setChapter(chapter);

        // Xử lý nội dung (Contents)
        List<LessonContent> contentList = new ArrayList<>();
        if (request.getContents() != null) {
            for (var contentReq : request.getContents()) {
                LessonContent content = new LessonContent();
                content.setContentType(contentReq.getType());
                content.setContentOrder(contentReq.getOrder());
                content.setContentValue(contentReq.getValue());
                content.setLesson(lesson);
                contentList.add(content);
            }
        }
        lesson.setContents(contentList);

        return lessonRepository.save(lesson);
    }

    // READ (GET LIST BY CHAPTER) - Thêm mới
    public List<Lesson> getLessonsByChapterId(Long chapterId) {
        // Đảm bảo LessonRepository có hàm: findByChapterIdOrderByOrderNoAsc(Long chapterId)
        return lessonRepository.findByChapterIdOrderByOrderNoAsc(chapterId);
    }

    // READ (GET DETAIL)
    public Lesson getLessonById(Long id) {
        return lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bài học không tồn tại với ID: " + id));
    }

    // UPDATE
    public Lesson updateLesson(Long lessonId, LessonRequest request) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Bài học không tồn tại với ID: " + lessonId));

        lesson.setTitle(request.getTitle());
        lesson.setVideoUrl(request.getVideoUrl());
        lesson.setOrderNo(request.getOrderNo());

        if (!lesson.getChapter().getId().equals(request.getChapterId())) {
            Chapter newChapter = chapterRepository.findById(request.getChapterId())
                    .orElseThrow(() -> new ResourceNotFoundException("Chương mới không tồn tại"));
            lesson.setChapter(newChapter);
        }

        if (request.getContents() != null) {
            lesson.getContents().clear();

            for (var contentReq : request.getContents()) {
                LessonContent content = new LessonContent();
                content.setContentType(contentReq.getType());
                content.setContentOrder(contentReq.getOrder());
                content.setContentValue(contentReq.getValue());
                content.setLesson(lesson);
                lesson.getContents().add(content);
            }
        }

        return lessonRepository.save(lesson);
    }

    // DELETE
    public void deleteLesson(Long lessonId) {
        if (!lessonRepository.existsById(lessonId)) {
            throw new ResourceNotFoundException("Bài học không tồn tại để xóa");
        }
        lessonRepository.deleteById(lessonId);
    }
}