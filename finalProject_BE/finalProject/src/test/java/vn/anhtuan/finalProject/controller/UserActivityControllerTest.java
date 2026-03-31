package vn.anhtuan.finalProject.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import vn.anhtuan.finalProject.dto.response.StreakResponse;
import vn.anhtuan.finalProject.service.UserActivityService;

import java.util.ArrayList;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserActivityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserActivityService userActivityService;

    @Test
    @WithMockUser(username = "testuser")
    void testGetMyStreak() throws Exception {
        StreakResponse response = new StreakResponse(5, 10, true, new ArrayList<>(), 100);
        when(userActivityService.getMyStreak()).thenReturn(response);

        mockMvc.perform(get("/api/activity/streak"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.currentStreak").value(5))
                .andExpect(jsonPath("$.data.totalMinutes").value(100));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testLogActivity() throws Exception {
        mockMvc.perform(post("/api/activity/log")
                        .param("minutes", "15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Ghi nhận hoạt động thành công"));

        verify(userActivityService).logActivity(15);
    }
}
