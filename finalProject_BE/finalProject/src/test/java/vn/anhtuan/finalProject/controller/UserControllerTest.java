package vn.anhtuan.finalProject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import vn.anhtuan.finalProject.dto.request.FcmTokenRequest;
import vn.anhtuan.finalProject.dto.request.PasswordChangeRequest;
import vn.anhtuan.finalProject.dto.request.UserProfileUpdateRequest;
import vn.anhtuan.finalProject.dto.response.UserProfileResponse;
import vn.anhtuan.finalProject.entity.User;
import vn.anhtuan.finalProject.service.IUserService;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IUserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void testChangePassword() throws Exception {
        PasswordChangeRequest request = new PasswordChangeRequest();
        request.setOldPassword("oldPass");
        request.setNewPassword("newPass");

        mockMvc.perform(put("/api/users/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Đổi mật khẩu thành công"));

        verify(userService, times(1)).changePassword(any(PasswordChangeRequest.class));
    }

    @Test
    @WithMockUser
    void testGetMyProfile() throws Exception {
        UserProfileResponse response = new UserProfileResponse();
        response.setUsername("testuser");
        response.setEmail("test@test.com");
        response.setDateOfBirth(LocalDate.of(2000, 1, 1));

        when(userService.getMyProfile()).thenReturn(response);

        mockMvc.perform(get("/api/users/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.email").value("test@test.com"));
    }

    @Test
    @WithMockUser
    void testUpdateMyProfile() throws Exception {
        UserProfileUpdateRequest request = new UserProfileUpdateRequest();
        request.setAvatarUrl("http://new-avatar.com");

        UserProfileResponse response = new UserProfileResponse();
        response.setAvatarUrl("http://new-avatar.com");

        when(userService.updateMyProfile(any(UserProfileUpdateRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/users/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.avatarUrl").value("http://new-avatar.com"));
    }

    @Test
    @WithMockUser
    void testUpdateFcmToken() throws Exception {
        FcmTokenRequest request = new FcmTokenRequest("new-fcm-token");

        mockMvc.perform(post("/api/users/update-fcm-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Cập nhật Token thành công"));

        verify(userService, times(1)).updateFcmToken("new-fcm-token");
    }
}
