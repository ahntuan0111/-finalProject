package vn.anhtuan.finalProject.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import vn.anhtuan.finalProject.entity.Document;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test") // Sử dụng cấu hình trong application-test.properties
class DocumentRepositoryTest {

    @Autowired
    private DocumentRepository documentRepository;

    @Test
    void testFindBySubjectCodeFlexibleAndGrade_ExactMatch() {
        // Arrange
        Document doc = new Document();
        doc.setTitleVi("Toán 10");
        doc.setSubjectCode("toan");
        doc.setGrade(10);
        doc.setFilePath("path/to.pdf");
        doc.setFileSize(1024L);
        documentRepository.save(doc);

        // Act
        List<Document> result = documentRepository.findBySubjectCodeFlexibleAndGrade("toan", 10);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitleVi()).isEqualTo("Toán 10");
    }

    @Test
    void testFindBySubjectCodeFlexibleAndGrade_CaseInsensitiveMatch() {
        // Arrange
        Document doc = new Document();
        doc.setTitleVi("Toán 10");
        doc.setSubjectCode("Toán");
        doc.setGrade(10);
        doc.setFilePath("path/to.pdf");
        doc.setFileSize(1024L);
        documentRepository.save(doc);

        // Act - Truy vấn bằng chữ thường "toán" -> Vẫn phải tìm ra
        List<Document> result = documentRepository.findBySubjectCodeFlexibleAndGrade("toán", 10);

        // Assert
        assertThat(result).hasSize(1);
    }

    @Test
    void testFindBySubjectCodeFlexibleAndGrade_LikeMatch() {
        // Arrange
        Document doc = new Document();
        doc.setTitleVi("Khoa học tự nhiên");
        doc.setSubjectCode("Khoa học tự nhiên");
        doc.setGrade(7);
        doc.setFilePath("path/to.pdf");
        doc.setFileSize(2048L);
        documentRepository.save(doc);

        // Act - Truy vấn chỉ bằng 1 phần mã "Khoa học"
        List<Document> result = documentRepository.findBySubjectCodeFlexibleAndGrade("Khoa học", 7);

        // Assert
        assertThat(result).hasSize(1);
    }
}
