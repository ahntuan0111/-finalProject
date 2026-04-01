package vn.anhtuan.finalProject.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import vn.anhtuan.finalProject.entity.Document;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test cho DocumentRepository.
 * Dùng @ActiveProfiles("test") để kết nối DB test.
 * Dùng @Transactional để tự rollback sau mỗi test (không làm bẩn DB).
 *
 * LƯU Ý: grade phải là 6, 7, 8, hoặc 9 vì có FK ràng buộc với bảng grades.
 *         subject_code phải là mã chuẩn hóa: "toan", "khtn", v.v.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class DocumentRepositoryTest {

    @Autowired
    private DocumentRepository documentRepository;

    // --- Helper tạo document test ---
    private Document buildDoc(String titleVi, String subjectCode, int grade) {
        Document doc = new Document();
        doc.setTitleVi(titleVi);
        doc.setSubjectCode(subjectCode);
        doc.setGrade(grade);  // Phải là 6, 7, 8 hoặc 9 (FK → grades.id)
        doc.setFilePath("https://firebase.example.com/test.pdf");
        doc.setFileSize(1024L);
        return doc;
    }

    /**
     * Test tìm chính xác theo mã chuẩn hóa (lowercase, không dấu).
     * Đây là use case chính sau khi đã migrate data.
     */
    @Test
    void testFindBySubjectCodeAndGrade_ExactMatch() {
        // Arrange
        documentRepository.save(buildDoc("Đề thi Toán Lớp 7", "toan", 7));

        // Act
        List<Document> result = documentRepository.findBySubjectCodeAndGrade("toan", 7);

        // Assert
        assertThat(result).isNotEmpty();
        assertThat(result).allMatch(d -> "toan".equals(d.getSubjectCode()) && d.getGrade() == 7);
    }

    /**
     * Test KHÔNG trả về kết quả khi subjectCode không khớp chính xác.
     * Sau khi chuẩn hóa, KHÔNG còn dùng LIKE query nữa.
     */
    @Test
    void testFindBySubjectCodeAndGrade_NoResultForWrongCode() {
        // Arrange: Lưu với mã chuẩn "toan"
        documentRepository.save(buildDoc("Đề thi Toán Lớp 7", "toan", 7));

        // Act: Query thử bằng tên không chuẩn "Toán" → Phải trả về rỗng
        List<Document> result = documentRepository.findBySubjectCodeAndGrade("Toán", 7);

        // Assert: ĐÚNG — Không tìm thấy vì đã chuẩn hóa về exact match
        assertThat(result).isEmpty();
    }

    /**
     * Test tìm đúng subject_code nhưng sai grade → không trả kết quả.
     */
    @Test
    void testFindBySubjectCodeAndGrade_WrongGrade() {
        // Arrange
        documentRepository.save(buildDoc("Đề thi Toán Lớp 7", "toan", 7));

        // Act: Query đúng code nhưng sai grade
        List<Document> result = documentRepository.findBySubjectCodeAndGrade("toan", 6);

        // Assert
        assertThat(result).isEmpty();
    }

    /**
     * Test tìm theo grade và nhiều môn học khác nhau.
     */
    @Test
    void testFindBySubjectCodeAndGrade_MultipleSubjects() {
        // Arrange
        documentRepository.save(buildDoc("Đề Toán Lớp 7", "toan", 7));
        documentRepository.save(buildDoc("Đề KHTN Lớp 7", "khtn", 7));
        documentRepository.save(buildDoc("Đề Toán Lớp 6", "toan", 6));

        // Act
        List<Document> result = documentRepository.findBySubjectCodeAndGrade("toan", 7);

        // Assert: Chỉ trả về Toán Lớp 7, không lẫn KHTN hay Lớp 6
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitleVi()).isEqualTo("Đề Toán Lớp 7");
    }

    /**
     * Test tìm kiếm theo keyword trong tiêu đề.
     */
    @Test
    void testSearchByKeywordAndGrade() {
        // Arrange
        documentRepository.save(buildDoc("Đề thi Giữa Kỳ Toán 7", "toan", 7));
        documentRepository.save(buildDoc("Đề thi Cuối Kỳ KHTN 7", "khtn", 7));

        // Act
        List<Document> result = documentRepository.searchByKeywordAndGrade("Giữa Kỳ", 7);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitleVi()).contains("Giữa Kỳ");
    }

    /**
     * Test findByGradeOrderBySubjectCodeAsc — lấy tất cả document trong grade.
     */
    @Test
    void testFindByGradeOrderBySubjectCodeAsc() {
        // Arrange
        documentRepository.save(buildDoc("Đề Toán", "toan", 8));
        documentRepository.save(buildDoc("Đề KHTN", "khtn", 8));

        // Act
        List<Document> result = documentRepository.findByGradeOrderBySubjectCodeAsc(8);

        // Assert
        assertThat(result).hasSizeGreaterThanOrEqualTo(2);
        // Kiểm tra thứ tự: "khtn" < "toan" theo alphabet
        assertThat(result.get(0).getSubjectCode()).isLessThanOrEqualTo(result.get(1).getSubjectCode());
    }
}
