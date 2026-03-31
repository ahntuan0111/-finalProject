package vn.anhtuan.finalProject.dto.request;

import lombok.Data;

@Data
public class AiChatRequest {
    private String message;  // Nội dung tin nhắn của học sinh

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
