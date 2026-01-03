package vn.anhtuan.finalProject.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.anhtuan.finalProject.dto.request.ForgotPasswordRequest;
import vn.anhtuan.finalProject.dto.request.LoginRequest;
import vn.anhtuan.finalProject.dto.request.RegisterRequest;
import vn.anhtuan.finalProject.dto.request.ResetPasswordRequest;
import vn.anhtuan.finalProject.dto.response.ApiResponse;
import vn.anhtuan.finalProject.dto.response.AuthResponse;
import vn.anhtuan.finalProject.service.IAuthService;
import vn.anhtuan.finalProject.service.impl.AuthServiceImpl;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final IAuthService authService;

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class); // Khai báo Logger

    public AuthController(IAuthService authService) {
        this.authService = authService;
    }

    @PostMapping(value = {"/login", "/signin"})
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse authResponse = authService.login(loginRequest);
        return ResponseEntity.ok(ApiResponse.success(authResponse, "Đăng nhập thành công"));
    }

    @PostMapping(value = {"/register", "/signup"})
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        // Ghi log kiểm tra dữ liệu đầu vào
        logger.info(">>> API REGISTER CALLED");
        logger.info("Payload: Username={}, Email={}", registerRequest.getUsername(), registerRequest.getEmail());

        try {
            String result = authService.register(registerRequest);
            logger.info(">>> REGISTER SUCCESS: {}", result);
            return new ResponseEntity<>(ApiResponse.success(result, "Đăng ký thành công"), HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error(">>> REGISTER FAILED: {}", e.getMessage());
            throw e; // Ném lại lỗi để GlobalExceptionHandler xử lý (nếu có)
        }
    }

    // --- API QUÊN MẬT KHẨU ---

    // 1. Gửi yêu cầu lấy mã OTP
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok(ApiResponse.success("Mã OTP đã được gửi (Check Console)", "Gửi mã thành công"));
    }

    // 2. Đặt lại mật khẩu với OTP
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success("Mật khẩu đã được thay đổi thành công", "Thành công"));
    }
}