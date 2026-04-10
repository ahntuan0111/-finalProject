package vn.anhtuan.finalProject.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import vn.anhtuan.finalProject.dto.response.AiChatMessageResponse;
import vn.anhtuan.finalProject.dto.response.AiChatResponse;
import vn.anhtuan.finalProject.entity.AiChatMessage;
import vn.anhtuan.finalProject.entity.AiChatSession;
import vn.anhtuan.finalProject.entity.User;
import vn.anhtuan.finalProject.repository.AiChatMessageRepository;
import vn.anhtuan.finalProject.repository.AiChatSessionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AiTutorService {

    private static final Logger logger = LoggerFactory.getLogger(AiTutorService.class);

    private static final String SYSTEM_PROMPT =
            "Bạn là một gia sư Toán học và Khoa học Tự nhiên tận tâm, vui tính dành cho học sinh cấp 2 tại Việt Nam.\n" +
            "Học sinh sẽ nhờ bạn giải một bài toán hoặc giải thích một khái niệm.\n\n" +
            "QUY TẮC TUYỆT ĐỐI BẮT BUỘC:\n" +
            "1. KHÔNG BAO GIỜ đưa ra đáp án cuối cùng hoặc giải toàn bộ bài toán ngay lập tức.\n" +
            "2. Hãy phân tích bài toán và hướng dẫn học sinh giải theo từng bước nhỏ một.\n" +
            "3. Ở mỗi bước, chỉ đưa ra một GỢI Ý NHỎ và kết thúc bằng một câu hỏi để học sinh tự tính toán.\n" +
            "4. Nếu học sinh tính sai, hãy nhẹ nhàng chỉ ra lỗi sai và yêu cầu tính lại.\n" +
            "5. Nếu học sinh nói 'Em không biết làm' hoặc 'Không hiểu', hãy chia nhỏ bước hiện tại thêm.\n" +
            "6. Nếu học sinh xin đáp án trực tiếp, hãy kiên quyết từ chối và tiếp tục gợi mở.\n" +
            "7. Dùng LaTeX cho công thức: $công_thức$ cho công thức ngắn và $$công_thức$$ cho công thức dài.\n" +
            "8. Luôn khen ngợi và khích lệ khi học sinh làm đúng một bước.";

    @Autowired
    @Qualifier("geminiRestClient")
    private RestClient geminiRestClient;

    @Autowired
    private AiChatSessionRepository sessionRepository;

    @Autowired
    private AiChatMessageRepository messageRepository;

    @Autowired
    private IUserService userService;

    @Transactional
    public AiChatResponse chat(String userMessage) {
        User currentUser = userService.getCurrentUser();

        // 1. Tìm hoặc tạo session mới
        AiChatSession session = sessionRepository
                .findTopByUserAndExerciseIdOrderByCreatedAtDesc(currentUser, 0L)
                .orElseGet(() -> {
                    AiChatSession newSession = new AiChatSession();
                    newSession.setUser(currentUser);
                    return sessionRepository.save(newSession);
                });

        // 2. Lấy lịch sử hội thoại cũ
        List<AiChatMessage> history = messageRepository.findBySessionOrderByCreatedAtAsc(session);

        // 3. Build payload Gemini API (contents array)
        List<Map<String, Object>> contents = buildContents(history, userMessage);

        // 4. Gọi Gemini API
        String aiReply = callGeminiApi(contents);

        // 5. Lưu tin nhắn user và AI vào DB
        AiChatMessage userMsg = new AiChatMessage();
        userMsg.setSession(session);
        userMsg.setRole("USER");
        userMsg.setContent(userMessage);
        messageRepository.save(userMsg);

        AiChatMessage aiMsg = new AiChatMessage();
        aiMsg.setSession(session);
        aiMsg.setRole("ASSISTANT");
        aiMsg.setContent(aiReply);
        messageRepository.save(aiMsg);

        return new AiChatResponse(aiReply, session.getId());
    }

    public List<AiChatMessageResponse> getHistory() {
        User currentUser = userService.getCurrentUser();
        return sessionRepository
                .findTopByUserAndExerciseIdOrderByCreatedAtDesc(currentUser, 0L)
                .map(session -> messageRepository.findBySessionOrderByCreatedAtAsc(session)
                        .stream()
                        .map(msg -> new AiChatMessageResponse(
                                msg.getId(),
                                msg.getCreatedAt(),
                                msg.getContent(),
                                msg.getRole()
                        ))
                        .collect(Collectors.toList()))
                .orElse(new ArrayList<>());
    }

    @Transactional
    public void clearHistory() {
        User currentUser = userService.getCurrentUser();
        sessionRepository.findTopByUserAndExerciseIdOrderByCreatedAtDesc(currentUser, 0L).ifPresent(session -> {
            messageRepository.deleteAll(messageRepository.findBySessionOrderByCreatedAtAsc(session));
            sessionRepository.delete(session);
        });
    }

    // ==========================================================================
    // PRIVATE HELPERS
    // ==========================================================================

    /**
     * Xây dựng mảng contents gửi lên Gemini:
     * - Phần tử đầu: System Prompt vai trò "user" + phản hồi "model"
     * - Toàn bộ lịch sử cũ
     * - Tin nhắn mới nhất của học sinh
     */
    private List<Map<String, Object>> buildContents(List<AiChatMessage> history, String newUserMsg) {
        List<Map<String, Object>> contents = new ArrayList<>();

        // System Prompt: Gemini dùng cặp user/model để thiết lập context
        contents.add(Map.of(
                "role", "user",
                "parts", List.of(Map.of("text", SYSTEM_PROMPT))
        ));
        contents.add(Map.of(
                "role", "model",
                "parts", List.of(Map.of("text", "Tôi hiểu rõ vai trò của mình. Tôi sẽ luôn hướng dẫn học sinh tự tư duy, không bao giờ đưa đáp án trực tiếp."))
        ));

        // Lịch sử cũ
        for (AiChatMessage msg : history) {
            String geminiRole = msg.getRole().equals("USER") ? "user" : "model";
            contents.add(Map.of(
                    "role", geminiRole,
                    "parts", List.of(Map.of("text", msg.getContent()))
            ));
        }

        // Tin nhắn mới
        contents.add(Map.of(
                "role", "user",
                "parts", List.of(Map.of("text", newUserMsg))
        ));

        return contents;
    }

    /**
     * Gọi Gemini REST API và trích xuất text từ response
     */
    @SuppressWarnings("unchecked")
    private String callGeminiApi(List<Map<String, Object>> contents) {
        try {
            Map<String, Object> requestBody = Map.of("contents", contents);

            Map<String, Object> response = geminiRestClient.post()
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

            // Parse: response.candidates[0].content.parts[0].text
            if (response != null && response.containsKey("candidates")) {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
                if (!candidates.isEmpty()) {
                    Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                    List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                    if (!parts.isEmpty()) {
                        return (String) parts.get(0).get("text");
                    }
                }
            }
            return "Xin lỗi, tôi gặp sự cố khi xử lý câu hỏi của bạn. Hãy thử lại nhé!";
        } catch (Exception e) {
            logger.error("Lỗi khi gọi Gemini API: {}", e.getMessage());
            return "Xin lỗi, dịch vụ AI đang gặp sự cố. Hãy thử lại sau ít phút nhé!";
        }
    }
}
