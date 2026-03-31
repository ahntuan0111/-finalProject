package vn.anhtuan.finalProject.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import vn.anhtuan.finalProject.dto.request.DocumentRequest;
import vn.anhtuan.finalProject.entity.Document;
import vn.anhtuan.finalProject.repository.DocumentRepository;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private FirebaseStorageService firebaseStorageService;

    @InjectMocks
    private DocumentService documentService;

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
    void testUploadDocument_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "content".getBytes());
        when(firebaseStorageService.uploadPdfToFirebase(any())).thenReturn("http://firebase.com/test.pdf");
        when(documentRepository.save(any(Document.class))).thenReturn(document);

        Document result = documentService.uploadDocument(file, "Tài liệu 1", "Doc 1", "Mô tả", "Desc", "toan", 10, "GK", "Midterm");

        assertNotNull(result);
        assertEquals("http://firebase.com/file.pdf", result.getFilePath());
        verify(documentRepository).save(any(Document.class));
    }

    @Test
    void testGetDocumentById_Success() {
        when(documentRepository.findById(1L)).thenReturn(Optional.of(document));
        Document result = documentService.getDocumentById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testUpdateDocument_Success() {
        DocumentRequest request = new DocumentRequest("Tên mới", "Midterm", "GK", 10, "toan", "Desc", "Mô tả", "New Title");
        when(documentRepository.findById(1L)).thenReturn(Optional.of(document));
        when(documentRepository.save(any(Document.class))).thenReturn(document);

        Document result = documentService.updateDocument(1L, request);
        assertNotNull(result);
        verify(documentRepository).save(any(Document.class));
    }

    @Test
    void testDeleteDocument_Success() {
        when(documentRepository.findById(1L)).thenReturn(Optional.of(document));
        documentService.deleteDocument(1L);
        verify(firebaseStorageService).deleteFileFromFirebase(anyString());
        verify(documentRepository).delete(document);
    }
}
