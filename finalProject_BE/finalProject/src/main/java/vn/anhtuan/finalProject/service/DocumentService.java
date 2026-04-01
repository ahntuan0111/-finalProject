package vn.anhtuan.finalProject.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.anhtuan.finalProject.dto.request.DocumentRequest;
import vn.anhtuan.finalProject.dto.response.DocumentResponse;
import vn.anhtuan.finalProject.entity.Document;
import vn.anhtuan.finalProject.repository.DocumentRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final FirebaseStorageService firebaseStorageService;

    public DocumentService(DocumentRepository documentRepository, FirebaseStorageService firebaseStorageService) {
        this.documentRepository = documentRepository;
        this.firebaseStorageService = firebaseStorageService;
    }

    /**
     * Upload tài liệu lên Firebase Storage và lưu metadata vào DB.
     * Lưu ý: subjectCode phải là mã chuẩn (vd: "toan", "khtn") — đã validated ở tầng Controller.
     */
    public Document uploadDocument(MultipartFile file, String titleVi, String titleEn,
                                   String descriptionVi, String descriptionEn,
                                   String subjectCode, Integer grade,
                                   String examTypeVi, String examTypeEn) {
        try {
            String firebaseUrl = firebaseStorageService.uploadPdfToFirebase(file);

            Document doc = new Document();
            doc.setTitleVi(titleVi);
            doc.setTitleEn(titleEn);
            doc.setDescriptionVi(descriptionVi);
            doc.setDescriptionEn(descriptionEn);
            doc.setExamTypeVi(examTypeVi);
            doc.setExamTypeEn(examTypeEn);
            doc.setFilePath(firebaseUrl);
            doc.setFileType(file.getContentType());
            doc.setFileSize(file.getSize());
            // Lưu mã chuẩn hóa (lowercase, không dấu) — ví dụ: "toan", "khtn"
            doc.setSubjectCode(subjectCode.toLowerCase().trim());
            doc.setGrade(grade);

            return documentRepository.save(doc);

        } catch (Exception ex) {
            throw new RuntimeException("Không thể upload file lên Firebase: " + ex.getMessage(), ex);
        }
    }

    /**
     * Lấy danh sách tài liệu theo môn học và khối lớp.
     * subjectCode phải là mã chuẩn (vd: "toan", "khtn").
     * Dữ liệu đã được chuẩn hóa bởi migration script → không cần alias map.
     */
    public List<DocumentResponse> getDocuments(String subjectCode, Integer grade, String lang) {
        List<Document> docs = documentRepository.findBySubjectCodeAndGrade(
                subjectCode.toLowerCase().trim(), grade
        );
        return docs.stream()
                .map(doc -> DocumentResponse.fromEntity(doc, lang))
                .collect(Collectors.toList());
    }

    /**
     * Lấy tất cả tài liệu (dành cho Admin).
     */
    public List<DocumentResponse> getAllDocuments(String lang) {
        return documentRepository.findAll().stream()
                .map(doc -> DocumentResponse.fromEntity(doc, lang))
                .collect(Collectors.toList());
    }

    /**
     * Tìm kiếm tài liệu theo từ khóa và grade.
     */
    public List<DocumentResponse> searchDocuments(String keyword, Integer grade, String lang) {
        List<Document> docs = documentRepository.searchByKeywordAndGrade(keyword, grade);
        return docs.stream()
                .map(doc -> DocumentResponse.fromEntity(doc, lang))
                .collect(Collectors.toList());
    }

    /**
     * Cập nhật thông tin tài liệu.
     */
    public Document updateDocument(Long id, DocumentRequest request) {
        Document existingDoc = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài liệu với ID: " + id));

        existingDoc.setTitleVi(request.getTitleVi());
        existingDoc.setTitleEn(request.getTitleEn());
        existingDoc.setDescriptionVi(request.getDescriptionVi());
        existingDoc.setDescriptionEn(request.getDescriptionEn());
        // Chuẩn hóa khi cập nhật
        existingDoc.setSubjectCode(request.getSubjectCode().toLowerCase().trim());
        existingDoc.setGrade(request.getGrade());
        existingDoc.setExamTypeVi(request.getExamTypeVi());
        existingDoc.setExamTypeEn(request.getExamTypeEn());

        return documentRepository.save(existingDoc);
    }

    /**
     * Xóa tài liệu và file trên Firebase Storage.
     */
    public void deleteDocument(Long id) {
        Document doc = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài liệu với ID: " + id));

        firebaseStorageService.deleteFileFromFirebase(doc.getFilePath());
        documentRepository.delete(doc);
    }

    public Document getDocumentById(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài liệu với ID: " + id));
    }
}