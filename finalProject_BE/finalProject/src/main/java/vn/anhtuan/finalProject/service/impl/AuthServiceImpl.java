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
import vn.anhtuan.finalProject.dto.response.AuthResponse;
import vn.anhtuan.finalProject.entity.PasswordResetToken;
import vn.anhtuan.finalProject.entity.Role;
import vn.anhtuan.finalProject.entity.User;
import vn.anhtuan.finalProject.exception.BadRequestException;
import vn.anhtuan.finalProject.exception.ResourceNotFoundException;
import vn.anhtuan.finalProject.repository.PasswordResetTokenRepository;
import vn.anhtuan.finalProject.repository.UserRepository;
import vn.anhtuan.finalProject.security.JwtTokenProvider;
import vn.anhtuan.finalProject.service.IAuthService;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class AuthServiceImpl implements IAuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class); // Khai báo Logger

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository; // Inject thêm Repository này
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           UserRepository userRepository,
                           PasswordResetTokenRepository tokenRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
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
        logger.info("--- Service: Bắt đầu xử lý đăng ký cho user: {}", registerRequest.getUsername());

        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            logger.warn("--- Service Error: Username {} đã tồn tại", registerRequest.getUsername());
            throw new BadRequestException("Username đã tồn tại!");
        }
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            logger.warn("--- Service Error: Email {} đã tồn tại", registerRequest.getEmail());
            throw new BadRequestException("Email đã tồn tại!");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(Role.USER);

        userRepository.save(user);
        logger.info("--- Service: Lưu User vào DB thành công với ID: {}", user.getId());

        return "Đăng ký thành công!";
    }

    // --- LOGIC QUÊN MẬT KHẨU ---

    @Override
    @Transactional // Cần Transactional để thực hiện xóa token cũ và thêm mới
    public void forgotPassword(ForgotPasswordRequest request) {
        // 1. Kiểm tra email có tồn tại không
        if (!userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceNotFoundException("Email không tồn tại trong hệ thống");
        }

        // 2. Xóa token cũ nếu có (để db sạch sẽ)
        tokenRepository.deleteByEmail(request.getEmail());

        // 3. Tạo OTP ngẫu nhiên 6 số
        String otp = String.format("%06d", new Random().nextInt(999999));

        // 4. Lưu vào bảng password_reset_tokens
        PasswordResetToken resetToken = new PasswordResetToken(
                otp,
                request.getEmail(),
                LocalDateTime.now().plusMinutes(15) // Hết hạn sau 15 phút
        );
        tokenRepository.save(resetToken);

        // 5. Gửi Email (Ở đây in ra console để test)
        // TODO: Tích hợp JavaMailSender để gửi thật
        System.out.println(">>> OTP SEND TO " + request.getEmail() + " : " + otp);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        // 1. Tìm Token trong DB
        PasswordResetToken resetToken = tokenRepository.findByToken(request.getOtp())
                .orElseThrow(() -> new BadRequestException("Mã OTP không chính xác"));

        // 2. Kiểm tra Token có khớp với Email gửi lên không
        if (!resetToken.getEmail().equals(request.getEmail())) {
            throw new BadRequestException("Mã OTP không hợp lệ cho email này");
        }

        // 3. Kiểm tra hết hạn
        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Mã OTP đã hết hạn");
        }

        // 4. Tìm User và đổi mật khẩu
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User không tồn tại"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // 5. Xóa token sau khi dùng xong
        tokenRepository.delete(resetToken);
    }
}