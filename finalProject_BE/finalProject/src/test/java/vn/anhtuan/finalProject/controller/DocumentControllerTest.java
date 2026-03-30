package vn.anhtuan.finalProject.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import vn.anhtuan.finalProject.dto.response.DocumentResponse;
import vn.anhtuan.finalProject.service.DocumentService;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class DocumentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DocumentService documentService;

    @InjectMocks
    private DocumentController documentController;

    @BeforeEach
    void setUp() {
        // Setup chạy Controller mà KHÔNG cần tải Spring Security/Filters
        mockMvc = MockMvcBuilders.standaloneSetup(documentController).build();
    }

    @Test
    void testGetDocuments_Success() throws Exception {
        // Arrange
        DocumentResponse res = new DocumentResponse();
        res.setId(10L);
        res.setTitle("Lịch sử 8");
        res.setSubjectCode("lich-su");
        
        when(documentService.getDocuments("lich-su", 8, "vi"))
                .thenReturn(Collections.singletonList(res));

        // Act & Assert
        // Gửi GET /api/documents?subjectCode=lich-su&grade=8
        mockMvc.perform(get("/api/documents")
                        .param("subjectCode", "lich-su")
                        .param("grade", "8")
                        .header("Accept-Language", "vi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value(10))
                .andExpect(jsonPath("$.data[0].title").value("Lịch sử 8"));
    }

    @Test
    void testGetDocuments_MissingGradeParam_ShouldReturn400() throws Exception {
        // Act & Assert
        // Cố tình bỏ quên tham số 'grade', expect HTTP 400 Bad Request
        mockMvc.perform(get("/api/documents")
                        .param("subjectCode", "lich-su"))
                .andExpect(status().isBadRequest());
    }
}
