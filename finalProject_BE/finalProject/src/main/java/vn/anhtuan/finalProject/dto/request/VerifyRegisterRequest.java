package vn.anhtuan.finalProject.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyRegisterRequest {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String otp;
}
