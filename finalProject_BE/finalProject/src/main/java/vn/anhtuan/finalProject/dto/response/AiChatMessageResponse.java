package vn.anhtuan.finalProject.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
public class AiChatMessageResponse {
    private Long id;
    private String role;          // "USER" hoặc "ASSISTANT"
    private String content;
    private LocalDateTime createdAt;

    public AiChatMessageResponse() {
    }

    public AiChatMessageResponse(Long id, LocalDateTime createdAt, String content, String role) {
        this.id = id;
        this.createdAt = createdAt;
        this.content = content;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}
