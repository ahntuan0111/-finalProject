package vn.anhtuan.finalProject.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AiChatResponse {
    private String reply;         // Câu trả lời từ AI
    private Long sessionId;       // ID phiên hội thoại
}
