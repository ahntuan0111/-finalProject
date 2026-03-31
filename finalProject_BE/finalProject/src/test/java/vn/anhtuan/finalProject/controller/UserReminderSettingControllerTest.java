package vn.anhtuan.finalProject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import vn.anhtuan.finalProject.dto.request.ReminderSettingRequest;
import vn.anhtuan.finalProject.dto.response.ReminderSettingResponse;
import vn.anhtuan.finalProject.service.UserReminderSettingService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserReminderSettingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserReminderSettingService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "testuser")
    void testGetMySettings() throws Exception {
        ReminderSettingResponse response = new ReminderSettingResponse();
        response.setStudyReminderEnabled(true);
        when(service.getMySettings()).thenReturn(response);

        mockMvc.perform(get("/api/users/reminders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.studyReminderEnabled").value(true));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testUpdateMySettings() throws Exception {
        ReminderSettingRequest request = new ReminderSettingRequest();
        request.setStudyReminderEnabled(true);

        ReminderSettingResponse response = new ReminderSettingResponse();
        response.setStudyReminderEnabled(true);

        when(service.updateMySettings(any(ReminderSettingRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/users/reminders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Cập nhật cấu hình nhắc nhở thành công"));
    }
}
