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
}