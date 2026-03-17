package vn.anhtuan.finalProject.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import vn.anhtuan.finalProject.dto.request.DocumentRequest;
import vn.anhtuan.finalProject.dto.response.DocumentResponse;
import vn.anhtuan.finalProject.entity.Document;
import vn.anhtuan.finalProject.repository.DocumentRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DocumentService {

    private final Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();
    private final DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Không thể tạo thư mục upload.", ex);
        }
    }

    public Document uploadDocument(MultipartFile file, String titleVi, String titleEn,
                                   String descriptionVi, String descriptionEn,
                                   String subjectCode, Integer grade,
                                   String examTypeVi, String examTypeEn) {
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileName = UUID.randomUUID().toString() + "_" + originalFileName;

        try {
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            String fileDownloadUri = "/api/documents/files/" + fileName;

            Document doc = new Document();
            doc.setTitleVi(titleVi);
            doc.setTitleEn(titleEn);
            doc.setDescriptionVi(descriptionVi);
            doc.setDescriptionEn(descriptionEn);
            doc.setExamTypeVi(examTypeVi);
            doc.setExamTypeEn(examTypeEn);

            doc.setFilePath(fileDownloadUri);
            doc.setFileType(file.getContentType());
            doc.setFileSize(file.getSize());
            doc.setSubjectCode(subjectCode);
            doc.setGrade(grade);

            return documentRepository.save(doc);

        } catch (IOException ex) {
            throw new RuntimeException("Không thể lưu file " + fileName, ex);
        }
    }

    public List<DocumentResponse> getDocuments(String subjectCode, Integer grade, String lang) {
        List<Document> docs = documentRepository.findBySubjectCodeAndGrade(subjectCode, grade);
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

        try {
            String[] pathParts = doc.getFilePath().split("/");
            String fileName = pathParts[pathParts.length - 1];
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            System.err.println("Lỗi khi xóa file vật lý: " + ex.getMessage());
        }

        documentRepository.delete(doc);
    }

    public Document getDocumentById(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài liệu với ID: " + id));
    }
}