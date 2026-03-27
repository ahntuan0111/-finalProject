package vn.anhtuan.finalProject.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.anhtuan.finalProject.dto.request.ForgotPasswordRequest;
import vn.anhtuan.finalProject.dto.request.LoginRequest;
import vn.anhtuan.finalProject.dto.request.RegisterRequest;
import vn.anhtuan.finalProject.dto.request.ResetPasswordRequest;
import vn.anhtuan.finalProject.dto.request.VerifyRegisterRequest;
import vn.anhtuan.finalProject.dto.response.AuthResponse;
import vn.anhtuan.finalProject.entity.PasswordResetToken;
import vn.anhtuan.finalProject.entity.RegistrationOtp;
import vn.anhtuan.finalProject.entity.Role;
import vn.anhtuan.finalProject.entity.User;
import vn.anhtuan.finalProject.exception.BadRequestException;
import vn.anhtuan.finalProject.exception.ResourceNotFoundException;
import vn.anhtuan.finalProject.repository.PasswordResetTokenRepository;
import vn.anhtuan.finalProject.repository.RegistrationOtpRepository;
import vn.anhtuan.finalProject.repository.UserRepository;
import vn.anhtuan.finalProject.security.JwtTokenProvider;
import vn.anhtuan.finalProject.service.IAuthService;
import vn.anhtuan.finalProject.service.IEmailService;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class AuthServiceImpl implements IAuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final RegistrationOtpRepository registrationOtpRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final IEmailService emailService;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           UserRepository userRepository,
                           PasswordResetTokenRepository tokenRepository,
                           RegistrationOtpRepository registrationOtpRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider,
                           IEmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.registrationOtpRepository = registrationOtpRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.emailService = emailService;
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String token = jwtTokenProvider.generateToken(user);

        return new AuthResponse(token, user.getId(), user.getUsername(), user.getEmail(), user.getRole().name());
    }

    @Override
    public String register(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new BadRequestException("Username đã tồn tại!");
        }
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BadRequestException("Email đã tồn tại!");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(Role.USER);

        userRepository.save(user);
        return "Đăng ký thành công!";
    }

    @Override
    @Transactional
    public void requestRegisterOtp(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername()) || registrationOtpRepository.existsByUsername(registerRequest.getUsername())) {
             throw new BadRequestException("Username đã tồn tại!");
        }
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
             throw new BadRequestException("Email đã tồn tại!");
        }

        registrationOtpRepository.deleteByEmail(registerRequest.getEmail());

        String otp = String.format("%06d", new Random().nextInt(999999));
        
        RegistrationOtp registrationOtp = new RegistrationOtp(
                registerRequest.getUsername(),
                registerRequest.getEmail(),
                passwordEncoder.encode(registerRequest.getPassword()),
                otp,
                LocalDateTime.now().plusMinutes(5)
        );
        registrationOtpRepository.save(registrationOtp);

        emailService.sendOtpEmail(registerRequest.getEmail(), otp);
        logger.info(">>> Registration OTP sent to {}: {}", registerRequest.getEmail(), otp);
    }

    @Override
    @Transactional
    public AuthResponse verifyRegisterOtp(VerifyRegisterRequest request) {
        RegistrationOtp regOtp = registrationOtpRepository.findByEmailAndOtp(request.getEmail(), request.getOtp())
                .orElseThrow(() -> new BadRequestException("Mã OTP không chính xác hoặc đã hết hạn"));

        if (regOtp.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Mã OTP đã hết hạn");
        }

        User user = new User();
        user.setUsername(regOtp.getUsername());
        user.setEmail(regOtp.getEmail());
        user.setPassword(regOtp.getPassword());
        user.setRole(Role.USER);
        userRepository.save(user);

        registrationOtpRepository.delete(regOtp);

        String token = jwtTokenProvider.generateToken(user);
        return new AuthResponse(token, user.getId(), user.getUsername(), user.getEmail(), user.getRole().name());
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        if (!userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceNotFoundException("Email không tồn tại trong hệ thống");
        }

        tokenRepository.deleteByEmail(request.getEmail());

        String otp = String.format("%06d", new Random().nextInt(999999));

        PasswordResetToken resetToken = new PasswordResetToken(
                otp,
                request.getEmail(),
                LocalDateTime.now().plusMinutes(15)
        );
        tokenRepository.save(resetToken);

        emailService.sendOtpEmail(request.getEmail(), otp);
        logger.info(">>> Forgot PW OTP sent to {}: {}", request.getEmail(), otp);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = tokenRepository.findByToken(request.getOtp())
                .orElseThrow(() -> new BadRequestException("Mã OTP không chính xác"));

        if (!resetToken.getEmail().equals(request.getEmail())) {
            throw new BadRequestException("Mã OTP không hợp lệ cho email này");
        }

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Mã OTP đã hết hạn");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User không tồn tại"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        tokenRepository.delete(resetToken);
    }
}