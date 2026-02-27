package vn.anhtuan.finalProject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import vn.anhtuan.finalProject.entity.Document;
import vn.anhtuan.finalProject.repository.DocumentRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentService {

    // Thư mục lưu file tạm thời trên server
    private final Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();

    @Autowired
    private DocumentRepository documentRepository;

    public DocumentService() {
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Không thể tạo thư mục upload.", ex);
        }
    }

    public Document uploadDocument(MultipartFile file, String title, String description,
                                   String subjectCode, Integer grade, String examType) {
        // 1. Chuẩn hóa tên file
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        // Tạo tên file ngẫu nhiên để tránh trùng lặp
        String fileName = UUID.randomUUID().toString() + "_" + originalFileName;

        try {
            // 2. Lưu file vào ổ cứng (Giai đoạn 1) -> Sau này thay bằng Cloud Upload
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // 3. Tạo đường dẫn truy cập (URL tương đối)
            String fileDownloadUri = "/api/documents/files/" + fileName;

            // 4. Lưu thông tin vào DB
            Document doc = new Document();
            doc.setTitle(title);
            doc.setDescription(description);
            doc.setFilePath(fileDownloadUri);
            doc.setFileType(file.getContentType());
            doc.setFileSize(file.getSize());
            doc.setSubjectCode(subjectCode);
            doc.setGrade(grade);
            doc.setExamType(examType);

            return documentRepository.save(doc);

        } catch (IOException ex) {
            throw new RuntimeException("Không thể lưu file " + fileName, ex);
        }
    }

    public List<Document> getDocuments(String subjectCode, Integer grade) {
        return documentRepository.findBySubjectCodeAndGrade(subjectCode, grade);
    }

    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    public Document updateDocument(Long id, Document updatedDocInfo) {
        Document existingDoc = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài liệu với ID: " + id));

        // Chỉ cập nhật các trường metadata, không cập nhật file vật lý qua API này
        existingDoc.setTitle(updatedDocInfo.getTitle());
        existingDoc.setDescription(updatedDocInfo.getDescription());
        existingDoc.setSubjectCode(updatedDocInfo.getSubjectCode());
        existingDoc.setGrade(updatedDocInfo.getGrade());
        existingDoc.setExamType(updatedDocInfo.getExamType());

        return documentRepository.save(existingDoc);
    }

    public void deleteDocument(Long id) {
        Document doc = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài liệu với ID: " + id));

        // 1. Xóa file vật lý trên server
        try {
            // Lấy tên file từ filePath (VD: "/api/documents/files/tenfile.pdf" -> "tenfile.pdf")
            String[] pathParts = doc.getFilePath().split("/");
            String fileName = pathParts[pathParts.length - 1];

            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            System.err.println("Lỗi khi xóa file vật lý: " + ex.getMessage());
            // Có thể chọn throw exception hoặc chỉ log lỗi và vẫn xóa trong DB
        }

        // 2. Xóa bản ghi trong Database
        documentRepository.delete(doc);
    }
}