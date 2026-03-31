package vn.anhtuan.finalProject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import vn.anhtuan.finalProject.dto.request.DocumentRequest;
import vn.anhtuan.finalProject.entity.Document;
import vn.anhtuan.finalProject.service.DocumentService;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DocumentService documentService;

    @Autowired
    private ObjectMapper objectMapper;

    private Document document;

    @BeforeEach
    void setUp() {
        document = new Document();
        document.setId(1L);
        document.setTitleVi("Tài liệu 1");
        document.setFilePath("http://firebase.com/file.pdf");
        document.setSubjectCode("toan");
        document.setGrade(10);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUploadFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "content".getBytes());

        // Use any() for all arguments to handle potential nulls for optional params
        when(documentService.uploadDocument(any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(document);

        mockMvc.perform(multipart("/api/documents/upload")
                        .file(file)
                        .param("titleVi", "Tài liệu 1")
                        .param("subjectCode", "toan")
                        .param("grade", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Tài liệu 1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetDocuments() throws Exception {
        when(documentService.getDocuments(anyString(), anyInt(), anyString()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/documents")
                        .param("subjectCode", "toan")
                        .param("grade", "10"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateDocument() throws Exception {
        DocumentRequest request = new DocumentRequest();
        request.setTitleVi("Mới");
        request.setSubjectCode("toan");
        request.setGrade(10);

        when(documentService.updateDocument(anyLong(), any(DocumentRequest.class))).thenReturn(document);

        mockMvc.perform(put("/api/documents/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteDocument() throws Exception {
        mockMvc.perform(delete("/api/documents/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Xóa tài liệu thành công"));
    }
}
