package vn.anhtuan.finalProject.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "registration_otps")
@Data
@NoArgsConstructor
public class RegistrationOtp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password; // Hash of password to be saved after verification

    @Column(nullable = false)
    private String otp;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    public RegistrationOtp(String username, String email, String password, String otp, LocalDateTime expiryDate) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.otp = otp;
        this.expiryDate = expiryDate;
    }
}
