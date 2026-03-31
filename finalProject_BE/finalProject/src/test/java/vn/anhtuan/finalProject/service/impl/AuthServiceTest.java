package vn.anhtuan.finalProject.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.anhtuan.finalProject.dto.request.*;
import vn.anhtuan.finalProject.dto.response.AuthResponse;
import vn.anhtuan.finalProject.entity.PasswordResetToken;
import vn.anhtuan.finalProject.entity.RegistrationOtp;
import vn.anhtuan.finalProject.entity.Role;
import vn.anhtuan.finalProject.entity.User;
import vn.anhtuan.finalProject.exception.BadRequestException;
import vn.anhtuan.finalProject.repository.PasswordResetTokenRepository;
import vn.anhtuan.finalProject.repository.RegistrationOtpRepository;
import vn.anhtuan.finalProject.repository.UserRepository;
import vn.anhtuan.finalProject.security.JwtTokenProvider;
import vn.anhtuan.finalProject.service.IEmailService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordResetTokenRepository tokenRepository;
    @Mock
    private RegistrationOtpRepository registrationOtpRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private IEmailService emailService;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("tuan123");
        registerRequest.setEmail("tuan@gmail.com");
        registerRequest.setPassword("123456");

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("tuan123");
        testUser.setEmail("tuan@gmail.com");
        testUser.setRole(Role.USER);
    }

    @Test
    void testRequestRegisterOtp_WhenUserExists_ShouldThrowException() {
        when(userRepository.existsByUsername(anyString())).thenReturn(true);
        assertThatThrownBy(() -> authService.requestRegisterOtp(registerRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Username đã tồn tại!");
    }

    @Test
    void testRequestRegisterOtp_Success() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed_password");

        authService.requestRegisterOtp(registerRequest);

        verify(registrationOtpRepository, times(1)).save(any(RegistrationOtp.class));
        verify(emailService, times(1)).sendOtpEmail(eq("tuan@gmail.com"), anyString());
    }

    @Test
    void testVerifyRegisterOtp_Success() {
        RegistrationOtp otp = new RegistrationOtp("tuan123", "tuan@gmail.com", "hash", "123456", LocalDateTime.now().plusMinutes(5));
        VerifyRegisterRequest verifyRequest = new VerifyRegisterRequest();
        verifyRequest.setEmail("tuan@gmail.com");
        verifyRequest.setOtp("123456");

        when(registrationOtpRepository.findByEmailAndOtp(anyString(), anyString())).thenReturn(Optional.of(otp));
        when(jwtTokenProvider.generateToken(any(User.class))).thenReturn("fake_jwt_token");

        AuthResponse response = authService.verifyRegisterOtp(verifyRequest);

        assertThat(response.getToken()).isEqualTo("fake_jwt_token");
        verify(userRepository, times(1)).save(any(User.class));
        verify(registrationOtpRepository, times(1)).delete(otp);
    }

    @Test
    void testLogin_Success() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("tuan123");
        loginRequest.setPassword("123456");

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(userRepository.findByUsername("tuan123")).thenReturn(Optional.of(testUser));
        when(jwtTokenProvider.generateToken(testUser)).thenReturn("jwt_token");

        AuthResponse response = authService.login(loginRequest);

        assertThat(response.getToken()).isEqualTo("jwt_token");
        assertThat(response.getUsername()).isEqualTo("tuan123");
    }

    @Test
    void testForgotPassword_Success() {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("tuan@gmail.com");
        when(userRepository.existsByEmail("tuan@gmail.com")).thenReturn(true);

        authService.forgotPassword(request);

        verify(tokenRepository, times(1)).save(any(PasswordResetToken.class));
        verify(emailService, times(1)).sendOtpEmail(eq("tuan@gmail.com"), anyString());
    }

    @Test
    void testResetPassword_Success() {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setEmail("tuan@gmail.com");
        request.setOtp("123456");
        request.setNewPassword("newpass123");
        PasswordResetToken token = new PasswordResetToken("123456", "tuan@gmail.com", LocalDateTime.now().plusMinutes(15));

        when(tokenRepository.findByToken("123456")).thenReturn(Optional.of(token));
        when(userRepository.findByEmail("tuan@gmail.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("newpass123")).thenReturn("new_hash");

        authService.resetPassword(request);

        assertThat(testUser.getPassword()).isEqualTo("new_hash");
        verify(tokenRepository, times(1)).delete(token);
    }
}
