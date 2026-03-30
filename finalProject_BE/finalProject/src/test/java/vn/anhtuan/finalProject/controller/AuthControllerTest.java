package vn.anhtuan.finalProject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import vn.anhtuan.finalProject.dto.request.LoginRequest;
import vn.anhtuan.finalProject.dto.request.RegisterRequest;
import vn.anhtuan.finalProject.dto.response.AuthResponse;
import vn.anhtuan.finalProject.service.IAuthService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IAuthService authService;

    @InjectMocks
    private AuthController authController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void testLogin_Success() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("tuan123");
        loginRequest.setPassword("123456");

        AuthResponse authResponse = new AuthResponse("jwt_token", 1L, "tuan123", "tuan@gmail.com", "USER");
        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").value("jwt_token"))
                .andExpect(jsonPath("$.data.username").value("tuan123"));
    }

    @Test
    void testRequestRegisterOtp_Success() throws Exception {
        // Arrange
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setEmail("new@gmail.com");
        registerRequest.setPassword("password");

        doNothing().when(authService).requestRegisterOtp(any(RegisterRequest.class));

        // Act & Assert
        mockMvc.perform(post("/api/auth/register-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("Mã OTP đã được gửi đến email của bạn"))
                .andExpect(jsonPath("$.message").value("Thành công"));
    }

    @Test
    void testLogin_InvalidRequest_ShouldReturn400() throws Exception {
        // Arrange: LoginRequest với username trống (giả sử bị @Valid chặn)
        // Lưu ý: StandaloneSetup không tự động kích hoạt Spring Validation sâu, 
        // nhưng ta có thể test cấu trúc trả về hoặc mock ném lỗi.
        LoginRequest invalidRequest = new LoginRequest();
        // Thiếu password

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
