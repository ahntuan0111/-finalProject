package vn.anhtuan.finalProject.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;
import vn.anhtuan.finalProject.service.FirebaseStorageService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FirebaseStorageService storageService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUploadFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "content".getBytes());
        when(storageService.uploadImageToFirebase(any(MultipartFile.class), anyString())).thenReturn("http://firebase.com/test.jpg");

        mockMvc.perform(multipart("/api/upload/image")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("http://firebase.com/test.jpg"));
    }
}
