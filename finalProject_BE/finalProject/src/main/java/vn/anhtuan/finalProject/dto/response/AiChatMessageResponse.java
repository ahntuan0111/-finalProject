package vn.anhtuan.finalProject.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class AiChatMessageResponse {
    private Long id;
    private String role;          // "USER" hoặc "ASSISTANT"
    private String content;
    private LocalDateTime createdAt;
}
