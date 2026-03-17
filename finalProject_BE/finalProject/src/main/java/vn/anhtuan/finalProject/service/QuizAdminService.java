package vn.anhtuan.finalProject.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.anhtuan.finalProject.dto.request.*;
import vn.anhtuan.finalProject.dto.response.QuizResponse;
import vn.anhtuan.finalProject.entity.*;
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

        // Cập nhật lưu đa ngôn ngữ
        quiz.setTitleVi(request.getTitleVi());
        quiz.setTitleEn(request.getTitleEn());

        if (request.getQuestions() != null) {
            for (QuestionRequest qRequest : request.getQuestions()) {

                Question question = new Question();
                question.setExplanationVi(qRequest.getExplanationVi());
                question.setExplanationEn(qRequest.getExplanationEn());
                question.setOrderNo(qRequest.getOrderNo() != null ? qRequest.getOrderNo() : 1);

                if (qRequest.getContents() != null) {
                    for (QuestionContentRequest cRequest : qRequest.getContents()) {
                        QuestionContent content = new QuestionContent();
                        content.setContentType(ContentType.valueOf(cRequest.getContentType()));
                        content.setContentValueVi(cRequest.getContentValueVi());
                        content.setContentValueEn(cRequest.getContentValueEn());
                        content.setContentOrder(cRequest.getContentOrder() != null ? cRequest.getContentOrder() : 1);
                        question.addContent(content);
                    }
                }

                if (qRequest.getChoices() != null) {
                    for (ChoiceRequest chRequest : qRequest.getChoices()) {
                        Choice choice = new Choice();
                        choice.setContentType(ContentType.valueOf(chRequest.getContentType()));
                        choice.setContentValueVi(chRequest.getContentValueVi());
                        choice.setContentValueEn(chRequest.getContentValueEn());
                        choice.setCorrect(chRequest.getCorrect());
                        question.addChoice(choice);
                    }
                }

                quiz.addQuestion(question);
            }
        }

        return quizRepository.save(quiz);
    }

    // Thêm tham số lang để Admin có thể xem được nội dung (mặc định truyền "vi" từ Controller xuống)
    public List<QuizResponse> getAllQuizzes(String lang) {
        List<Quiz> quizzes = quizRepository.findAll();

        return quizzes.stream()
                .map(quiz -> QuizResponse.fromEntity(quiz, lang))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteQuiz(Integer id) {
        if (!quizRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy Quiz với ID: " + id);
        }
        quizRepository.deleteById(id);
    }

    public List<QuizType> getAllQuizTypes() {
        return quizTypeRepository.findAll();
    }

    public List<Chapter> getAllChapters() {
        return chapterRepository.findAll();
    }

    @Transactional
    public Quiz updateQuiz(Integer id, QuizRequest request) {
        Quiz existingQuiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài Quiz với ID: " + id));

        if (!existingQuiz.getCode().equals(request.getCode()) && quizRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Mã Code này đã được sử dụng cho một bài Quiz khác!");
        }

        Chapter chapter = chapterRepository.findById(request.getChapterId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Chương học!"));
        QuizType quizType = quizTypeRepository.findById(request.getQuizTypeId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy QuizType!"));

        existingQuiz.setChapter(chapter);
        existingQuiz.setQuizType(quizType);
        existingQuiz.setCode(request.getCode());

        existingQuiz.setTitleVi(request.getTitleVi());
        existingQuiz.setTitleEn(request.getTitleEn());

        if (request.getQuestions() != null) {
            Map<Long, Question> existingQuestionMap = existingQuiz.getQuestions().stream()
                    .collect(Collectors.toMap(Question::getId, q -> q));

            existingQuiz.getQuestions().clear();

            for (QuestionRequest qReq : request.getQuestions()) {
                Question question;
                if (qReq.getId() != null && existingQuestionMap.containsKey(qReq.getId())) {
                    question = existingQuestionMap.get(qReq.getId());
                } else {
                    question = new Question();
                }

                question.setExplanationVi(qReq.getExplanationVi());
                question.setExplanationEn(qReq.getExplanationEn());
                question.setOrderNo(qReq.getOrderNo() != null ? qReq.getOrderNo() : 1);

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
                        content.setContentValueVi(cReq.getContentValueVi());
                        content.setContentValueEn(cReq.getContentValueEn());
                        content.setContentOrder(cReq.getContentOrder() != null ? cReq.getContentOrder() : 1);
                        question.addContent(content);
                    }
                }

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
                        choice.setContentValueVi(chReq.getContentValueVi());
                        choice.setContentValueEn(chReq.getContentValueEn());
                        choice.setCorrect(chReq.getCorrect());
                        question.addChoice(choice);
                    }
                }

                existingQuiz.addQuestion(question);
            }
        } else {
            existingQuiz.getQuestions().clear();
        }

        return quizRepository.save(existingQuiz);
    }

    // Lấy chi tiết 1 bài Quiz (Dành cho Admin)
    public Quiz getQuizById(Integer id) {
        return quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài Quiz với ID: " + id));
    }

    // Lấy toàn bộ danh sách Quiz (Dữ liệu Entity thô, chưa lọc ngôn ngữ)
    public List<Quiz> getAllQuizzesRaw() {
        return quizRepository.findAll();
    }
}