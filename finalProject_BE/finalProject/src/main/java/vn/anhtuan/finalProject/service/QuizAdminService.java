package vn.anhtuan.finalProject.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.anhtuan.finalProject.dto.request.*;
import vn.anhtuan.finalProject.entity.*;
import vn.anhtuan.finalProject.entity.ContentType;
import vn.anhtuan.finalProject.repository.ChapterRepository;
import vn.anhtuan.finalProject.repository.QuizRepository;
import vn.anhtuan.finalProject.repository.QuizTypeRepository;

import java.util.List;

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

    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
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
}