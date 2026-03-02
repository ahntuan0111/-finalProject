package vn.anhtuan.finalProject.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.anhtuan.finalProject.dto.request.*;
import vn.anhtuan.finalProject.dto.response.QuizResponse;
import vn.anhtuan.finalProject.entity.*;
import vn.anhtuan.finalProject.entity.ContentType;
import vn.anhtuan.finalProject.repository.ChapterRepository;
import vn.anhtuan.finalProject.repository.QuizRepository;
import vn.anhtuan.finalProject.repository.QuizTypeRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QuizAdminService {

    private final QuizRepository quizRepository;
    private final QuizTypeRepository quizTypeRepository;
    private final ChapterRepository chapterRepository;

    public QuizAdminService(QuizRepository quizRepository, QuizTypeRepository quizTypeRepository, ChapterRepository chapterRepository) {
        this.quizRepository = quizRepository;
        this.quizTypeRepository = quizTypeRepository;
        this.chapterRepository = chapterRepository;
    }

    @Transactional
    public Quiz createQuiz(QuizRequest request) {
        if (quizRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Mã Code của Quiz đã tồn tại!");
        }

        QuizType quizType = quizTypeRepository.findById(request.getQuizTypeId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy QuizType!"));

        Chapter chapter = chapterRepository.findById(request.getChapterId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Chương học!"));

        Quiz quiz = new Quiz();
        quiz.setChapter(chapter);
        quiz.setQuizType(quizType);
        quiz.setCode(request.getCode());
        quiz.setTitle(request.getTitle()); // Map tiêu đề mới

        if (request.getQuestions() != null) {
            for (QuestionRequest qRequest : request.getQuestions()) {

                Question question = new Question();
                question.setExplanation(qRequest.getExplanation());
                // Gán mặc định là 1 nếu Front-end không truyền xuống
                question.setOrderNo(qRequest.getOrderNo() != null ? qRequest.getOrderNo() : 1);

                if (qRequest.getContents() != null) {
                    for (QuestionContentRequest cRequest : qRequest.getContents()) {

                        QuestionContent content = new QuestionContent();
                        content.setContentType(ContentType.valueOf(cRequest.getContentType()));
                        content.setContentValue(cRequest.getContentValue());
                        content.setContentOrder(cRequest.getContentOrder() != null ? cRequest.getContentOrder() : 1);

                        question.addContent(content);
                    }
                }

                if (qRequest.getChoices() != null) {
                    for (ChoiceRequest chRequest : qRequest.getChoices()) {

                        Choice choice = new Choice();
                        // Hỗ trợ cả TEXT và IMAGE cho đáp án
                        choice.setContentType(ContentType.valueOf(chRequest.getContentType()));
                        choice.setContentValue(chRequest.getContentValue());
                        choice.setCorrect(chRequest.getCorrect());

                        question.addChoice(choice);
                    }
                }

                quiz.addQuestion(question);
            }
        }

        return quizRepository.save(quiz);
    }

    public List<QuizResponse> getAllQuizzes() {
        List<Quiz> quizzes = quizRepository.findAll();

        return quizzes.stream()
                .map(QuizResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteQuiz(Integer id) {
        if (!quizRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy Quiz với ID: " + id);
        }
        quizRepository.deleteById(id);
    }

    // Lấy danh sách loại Quiz cho Dropdown
    public List<QuizType> getAllQuizTypes() {
        return quizTypeRepository.findAll();
    }

    // Lấy danh sách Chương học cho Dropdown
    public List<Chapter> getAllChapters() {
        return chapterRepository.findAll();
    }

    @Transactional
    public Quiz updateQuiz(Integer id, QuizRequest request) {
        // 1. Tìm Quiz cũ trong DB
        Quiz existingQuiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài Quiz với ID: " + id));

        // Kiểm tra xem mã code mới có bị trùng với Quiz KHÁC không
        if (!existingQuiz.getCode().equals(request.getCode()) && quizRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Mã Code này đã được sử dụng cho một bài Quiz khác!");
        }

        // 2. Cập nhật thông tin cơ bản của Quiz
        Chapter chapter = chapterRepository.findById(request.getChapterId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Chương học!"));
        QuizType quizType = quizTypeRepository.findById(request.getQuizTypeId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy QuizType!"));

        existingQuiz.setChapter(chapter);
        existingQuiz.setQuizType(quizType);
        existingQuiz.setCode(request.getCode());
        existingQuiz.setTitle(request.getTitle());

        // ====================================================================
        // 3. SMART MERGE CHO DANH SÁCH CÂU HỎI (QUESTIONS)
        // ====================================================================
        if (request.getQuestions() != null) {
            // Tạo Map các câu hỏi cũ để tra cứu nhanh bằng ID
            Map<Long, Question> existingQuestionMap = existingQuiz.getQuestions().stream()
                    .collect(Collectors.toMap(Question::getId, q -> q));

            // Xóa rỗng danh sách hiện tại (Hibernate sẽ tự động xóa những câu không được add lại nhờ orphanRemoval=true)
            existingQuiz.getQuestions().clear();

            for (QuestionRequest qReq : request.getQuestions()) {
                Question question;
                // Nếu Frontend gửi lên ID và ID đó có trong DB -> Đây là câu hỏi cũ cần Cập nhật
                if (qReq.getId() != null && existingQuestionMap.containsKey(qReq.getId())) {
                    question = existingQuestionMap.get(qReq.getId());
                } else {
                    // Nếu không có ID -> Đây là câu hỏi Mới được thêm vào
                    question = new Question();
                }

                question.setExplanation(qReq.getExplanation());
                question.setOrderNo(qReq.getOrderNo() != null ? qReq.getOrderNo() : 1);

                // --- 3.1 SMART MERGE CHO NỘI DUNG ĐỀ BÀI (CONTENTS) ---
                if (qReq.getContents() != null) {
                    Map<Long, QuestionContent> existingContentMap = question.getContents().stream()
                            .collect(Collectors.toMap(QuestionContent::getId, c -> c));

                    question.getContents().clear();

                    for (QuestionContentRequest cReq : qReq.getContents()) {
                        QuestionContent content;
                        if (cReq.getId() != null && existingContentMap.containsKey(cReq.getId())) {
                            content = existingContentMap.get(cReq.getId());
                        } else {
                            content = new QuestionContent();
                        }
                        content.setContentType(ContentType.valueOf(cReq.getContentType()));
                        content.setContentValue(cReq.getContentValue());
                        content.setContentOrder(cReq.getContentOrder() != null ? cReq.getContentOrder() : 1);
                        question.addContent(content);
                    }
                }

                // --- 3.2 SMART MERGE CHO ĐÁP ÁN (CHOICES) ---
                if (qReq.getChoices() != null) {
                    Map<Long, Choice> existingChoiceMap = question.getChoices().stream()
                            .collect(Collectors.toMap(Choice::getId, ch -> ch));

                    question.getChoices().clear();

                    for (ChoiceRequest chReq : qReq.getChoices()) {
                        Choice choice;
                        if (chReq.getId() != null && existingChoiceMap.containsKey(chReq.getId())) {
                            choice = existingChoiceMap.get(chReq.getId());
                        } else {
                            choice = new Choice();
                        }
                        choice.setContentType(ContentType.valueOf(chReq.getContentType()));
                        choice.setContentValue(chReq.getContentValue());
                        choice.setCorrect(chReq.getCorrect());
                        question.addChoice(choice);
                    }
                }

                // Gắn câu hỏi (đã cập nhật/tạo mới) vào Quiz
                existingQuiz.addQuestion(question);
            }
        } else {
            // Nếu Frontend gửi mảng rỗng -> Xóa toàn bộ câu hỏi
            existingQuiz.getQuestions().clear();
        }

        // Lưu lại (JPA sẽ tự động phân giải cái nào là INSERT, cái nào UPDATE, cái nào DELETE)
        return quizRepository.save(existingQuiz);
    }
}