package vn.anhtuan.finalProject.dto.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UserProfileUpdateRequest {
    private String avatarUrl;
    private LocalDate dateOfBirth;
    // Có thể thêm phone, fullname... nếu sau này Entity User của bạn cập nhật thêm


    public UserProfileUpdateRequest(String avatarUrl, LocalDate dateOfBirth) {
        this.avatarUrl = avatarUrl;
        this.dateOfBirth = dateOfBirth;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
