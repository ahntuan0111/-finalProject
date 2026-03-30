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

    public Document uploadDocument(MultipartFile file, String titleVi, String titleEn,
                                   String descriptionVi, String descriptionEn,
                                   String subjectCode, Integer grade,
                                   String examTypeVi, String examTypeEn) {
        try {
            // Upload lên Firebase Storage thay vì lưu local
            String firebaseUrl = firebaseStorageService.uploadPdfToFirebase(file);

            Document doc = new Document();
            doc.setTitleVi(titleVi);
            doc.setTitleEn(titleEn);
            doc.setDescriptionVi(descriptionVi);
            doc.setDescriptionEn(descriptionEn);
            doc.setExamTypeVi(examTypeVi);
            doc.setExamTypeEn(examTypeEn);

            // Lưu URL của Firebase vào filePath
            doc.setFilePath(firebaseUrl);
            doc.setFileType(file.getContentType());
            doc.setFileSize(file.getSize());
            doc.setSubjectCode(subjectCode);
            doc.setGrade(grade);

            return documentRepository.save(doc);

        } catch (Exception ex) {
            throw new RuntimeException("Không thể upload file lên Firebase: " + ex.getMessage(), ex);
        }
    }

    // Map các mã ngắn (mobile gửi lên) sang giá trị trong DB (web admin lưu)
    private static final java.util.Map<String, List<String>> SUBJECT_CODE_ALIASES;
    static {
        SUBJECT_CODE_ALIASES = new java.util.HashMap<>();
        SUBJECT_CODE_ALIASES.put("toan", java.util.Arrays.asList("toan", "Toán", "Toan", "TOAN", "math"));
        SUBJECT_CODE_ALIASES.put("khtn", java.util.Arrays.asList("khtn", "Khoa học tự nhiên", "KHTN", "science"));
        SUBJECT_CODE_ALIASES.put("ngu-van", java.util.Arrays.asList("ngu-van", "Ngữ văn", "ngu_van"));
        SUBJECT_CODE_ALIASES.put("tieng-anh", java.util.Arrays.asList("tieng-anh", "Tiếng Anh", "english"));
    }

    public List<DocumentResponse> getDocuments(String subjectCode, Integer grade, String lang) {
        // Dùng query linh hoạt để hỗ trợ cả mã nắn ("toan") và tên đầy đủ ("Toán")
        List<Document> docs = documentRepository.findBySubjectCodeFlexibleAndGrade(subjectCode, grade);

        // Nếu không tìm thấy, thử tìm theo alias
        if (docs.isEmpty()) {
            List<String> aliases = SUBJECT_CODE_ALIASES.getOrDefault(subjectCode.toLowerCase(), java.util.Collections.emptyList());
            for (String alias : aliases) {
                if (!alias.equals(subjectCode)) {
                    docs = documentRepository.findBySubjectCodeFlexibleAndGrade(alias, grade);
                    if (!docs.isEmpty()) break;
                }
            }
        }

        return docs.stream()
                .map(doc -> DocumentResponse.fromEntity(doc, lang))
                .collect(Collectors.toList());
    }

    public List<DocumentResponse> getAllDocuments(String lang) {
        return documentRepository.findAll().stream()
                .map(doc -> DocumentResponse.fromEntity(doc, lang))
                .collect(Collectors.toList());
    }

    public Document updateDocument(Long id, DocumentRequest request) {
        Document existingDoc = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài liệu với ID: " + id));

        existingDoc.setTitleVi(request.getTitleVi());
        existingDoc.setTitleEn(request.getTitleEn());
        existingDoc.setDescriptionVi(request.getDescriptionVi());
        existingDoc.setDescriptionEn(request.getDescriptionEn());
        existingDoc.setSubjectCode(request.getSubjectCode());
        existingDoc.setGrade(request.getGrade());
        existingDoc.setExamTypeVi(request.getExamTypeVi());
        existingDoc.setExamTypeEn(request.getExamTypeEn());

        return documentRepository.save(existingDoc);
    }

    public void deleteDocument(Long id) {
        Document doc = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài liệu với ID: " + id));

        // Xóa file trên Firebase Storage nếu URL là Firebase URL
        firebaseStorageService.deleteFileFromFirebase(doc.getFilePath());

        documentRepository.delete(doc);
    }

    public Document getDocumentById(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài liệu với ID: " + id));
    }
}