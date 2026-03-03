package vn.anhtuan.finalProject.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data

public class UserProfileResponse {
    private Long id;
    private String username;
    private String email;
    private String avatarUrl;
    private LocalDate dateOfBirth;
    // Tạm thời các chỉ số Stats (Điểm TB, Số giờ học) sẽ được tính toán sau,
    // hiện tại trả về thông tin cơ bản trước.

    public UserProfileResponse(Long id, String username, String email, String avatarUrl, LocalDate dateOfBirth) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.avatarUrl = avatarUrl;
        this.dateOfBirth = dateOfBirth;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
