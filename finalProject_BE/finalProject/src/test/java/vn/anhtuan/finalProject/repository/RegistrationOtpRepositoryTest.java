package vn.anhtuan.finalProject.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import vn.anhtuan.finalProject.entity.RegistrationOtp;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RegistrationOtpRepositoryTest {

    @Autowired
    private RegistrationOtpRepository otpRepository;

    @Test
    void testSaveAndFindByEmailAndOtp() {
        RegistrationOtp otp = new RegistrationOtp();
        otp.setEmail("test@example.com");
        otp.setUsername("testuser");
        otp.setOtp("123456");
        otp.setPassword("password");
        otp.setExpiryDate(LocalDateTime.now().plusMinutes(5));
        otpRepository.save(otp);

        Optional<RegistrationOtp> found = otpRepository.findByEmailAndOtp("test@example.com", "123456");
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    void testDeleteByEmail() {
        RegistrationOtp otp = new RegistrationOtp();
        otp.setEmail("delete@test.com");
        otp.setUsername("delete_user");
        otp.setPassword("password");
        otp.setOtp("111222");
        otp.setExpiryDate(LocalDateTime.now().plusMinutes(5));
        otpRepository.save(otp);

        otpRepository.deleteByEmail("delete@test.com");
        Optional<RegistrationOtp> found = otpRepository.findByEmailAndOtp("delete@test.com", "111222");
        assertThat(found).isNotPresent();
    }

    @Test
    void testExistsByEmailAndUsername() {
        RegistrationOtp otp = new RegistrationOtp();
        otp.setEmail("exists@test.com");
        otp.setUsername("exist_user");
        otp.setPassword("password");
        otp.setOtp("000000");
        otp.setExpiryDate(LocalDateTime.now().plusMinutes(5));
        otpRepository.save(otp);

        assertThat(otpRepository.existsByEmail("exists@test.com")).isTrue();
        assertThat(otpRepository.existsByUsername("exist_user")).isTrue();
        assertThat(otpRepository.existsByEmail("none@test.com")).isFalse();
    }
}
