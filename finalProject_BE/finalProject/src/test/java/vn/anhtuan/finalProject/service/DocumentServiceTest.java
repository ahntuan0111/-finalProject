package vn.anhtuan.finalProject.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.anhtuan.finalProject.dto.response.DocumentResponse;
import vn.anhtuan.finalProject.entity.Document;
import vn.anhtuan.finalProject.repository.DocumentRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private FirebaseStorageService firebaseStorageService;

    @InjectMocks
    private DocumentService documentService;

    private Document dummyDoc;

    @BeforeEach
    void setUp() {
        dummyDoc = new Document();
        dummyDoc.setId(1L);
        dummyDoc.setTitleVi("Toán Học Đại Số");
        dummyDoc.setSubjectCode("Toán");
        dummyDoc.setGrade(10);
        dummyDoc.setFilePath("https://firebase.url/test.pdf");
    }

    @Test
    void testGetDocuments_DirectMatch() {
        // Arrange
        when(documentRepository.findBySubjectCodeFlexibleAndGrade("toan", 10))
                .thenReturn(Collections.singletonList(dummyDoc));

        // Act
        List<DocumentResponse> result = documentService.getDocuments("toan", 10, "vi");

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Toán Học Đại Số");

        // Đảm bảo chỉ gọi DB đúng 1 lần
        verify(documentRepository, times(1)).findBySubjectCodeFlexibleAndGrade(anyString(), anyInt());
    }

    @Test
    void testGetDocuments_FallbackToAlias() {
        // Arrange
        // Lần đầu gọi mã ngắn "khtn" không ra gì
        when(documentRepository.findBySubjectCodeFlexibleAndGrade("khtn", 7))
                .thenReturn(Collections.emptyList());
        
        // Lần sau vòng lặp dò tìm gọi alias "Khoa học tự nhiên" thì ra kết quả
        Document doc2 = new Document();
        doc2.setTitleVi("KHTN 7");
        when(documentRepository.findBySubjectCodeFlexibleAndGrade("Khoa học tự nhiên", 7))
                .thenReturn(Collections.singletonList(doc2));

        // Act
        List<DocumentResponse> result = documentService.getDocuments("khtn", 7, "vi");

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("KHTN 7");

        // Verify gọi ít nhất 2 lần (1 cho khtn, 1 cho alias match)
        verify(documentRepository, atLeast(2)).findBySubjectCodeFlexibleAndGrade(anyString(), anyInt());
    }
}
