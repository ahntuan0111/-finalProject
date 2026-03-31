package vn.anhtuan.finalProject.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
public class AiChatResponse {
    private String reply;         // Câu trả lời từ AI
    private Long sessionId;       // ID phiên hội thoại

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public AiChatResponse() {
    }

    public AiChatResponse(String reply, Long sessionId) {
        this.reply = reply;
        this.sessionId = sessionId;
    }
}
