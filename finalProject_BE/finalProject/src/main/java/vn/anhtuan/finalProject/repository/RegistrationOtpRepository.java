package vn.anhtuan.finalProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.anhtuan.finalProject.entity.RegistrationOtp;

import java.util.Optional;

@Repository
public interface RegistrationOtpRepository extends JpaRepository<RegistrationOtp, Long> {
    Optional<RegistrationOtp> findByEmailAndOtp(String email, String otp);
    void deleteByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
