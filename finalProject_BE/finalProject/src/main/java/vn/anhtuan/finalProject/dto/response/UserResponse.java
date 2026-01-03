package vn.anhtuan.finalProject.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor // Giữ lại constructor rỗng cho các thư viện khác (Jackson, Hibernate)
public class UserResponse {

    private Long id;
    private String username;
    private String email;
    private String role;
    private LocalDateTime createdAt;

    // THÊM: Constructor thủ công để đảm bảo thứ tự chính xác 100%
    public UserResponse(Long id, String username, String email, String role, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.createdAt = createdAt;
    }
}