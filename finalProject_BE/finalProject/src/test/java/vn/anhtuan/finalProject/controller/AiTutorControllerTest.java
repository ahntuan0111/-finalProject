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
import vn.anhtuan.finalProject.dto.request.AiChatRequest;
import vn.anhtuan.finalProject.dto.response.AiChatResponse;
import vn.anhtuan.finalProject.service.AiTutorService;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AiTutorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AiTutorService aiTutorService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "testuser")
    void testChat() throws Exception {
        AiChatRequest request = new AiChatRequest();
        request.setMessage("Hello AI");

        AiChatResponse response = new AiChatResponse("Hello Student", 1L);

        when(aiTutorService.chat(anyString())).thenReturn(response);

        mockMvc.perform(post("/api/ai/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.reply").value("Hello Student"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetHistory() throws Exception {
        when(aiTutorService.getHistory()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/ai/chat/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Lấy lịch sử thành công"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testClearHistory() throws Exception {
        mockMvc.perform(delete("/api/ai/chat/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Đã xoá lịch sử hội thoại thành công"));
    }
}
