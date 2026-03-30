package vn.anhtuan.finalProject.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.anhtuan.finalProject.dto.request.DocumentRequest;
import vn.anhtuan.finalProject.dto.response.ApiResponse;
import vn.anhtuan.finalProject.dto.response.DocumentResponse;
import vn.anhtuan.finalProject.dto.response.admin.DocumentAdminResponse;
import vn.anhtuan.finalProject.entity.Document;
import vn.anhtuan.finalProject.service.DocumentService;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    // API Upload (Dành cho Web Admin - Multipart/form-data)
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("titleVi") String titleVi,
            @RequestParam(value = "titleEn", required = false) String titleEn,
            @RequestParam(value = "descriptionVi", required = false) String descriptionVi,
            @RequestParam(value = "descriptionEn", required = false) String descriptionEn,
            @RequestParam("subjectCode") String subjectCode,
            @RequestParam("grade") Integer grade,
            @RequestParam(value = "examTypeVi", required = false) String examTypeVi,
            @RequestParam(value = "examTypeEn", required = false) String examTypeEn,
            @RequestHeader(name = "Accept-Language", defaultValue = "vi") String lang) {

        Document doc = documentService.uploadDocument(file, titleVi, titleEn, descriptionVi, descriptionEn, subjectCode, grade, examTypeVi, examTypeEn);
        // Trả về DTO 1 ngôn ngữ sau khi upload thành công
        return ResponseEntity.ok(ApiResponse.success(DocumentResponse.fromEntity(doc, lang), "Upload thành công"));
    }

    // API Get List (Dành cho Android App)
    @GetMapping
    public ResponseEntity<?> getDocuments(
            @RequestParam("subjectCode") String subjectCode,
            @RequestParam("grade") Integer grade,
            @RequestHeader(name = "Accept-Language", defaultValue = "vi") String lang) {

        List<DocumentResponse> docs = documentService.getDocuments(subjectCode, grade, lang);
        return ResponseEntity.ok(ApiResponse.success(docs, "Lấy danh sách tài liệu thành công"));
    }

    // API Lấy tất cả tài liệu (Dành cho Web Admin)
    @GetMapping("/all")
    public ResponseEntity<?> getAllDocuments(
            @RequestHeader(name = "Accept-Language", defaultValue = "vi") String lang) {

        List<DocumentResponse> docs = documentService.getAllDocuments(lang);
        return ResponseEntity.ok(ApiResponse.success(docs, "Lấy danh sách tài liệu thành công"));
    }

    // API Lấy chi tiết tài liệu (Dành cho Web Admin để Edit - Trả về full 2 ngôn ngữ)
    @GetMapping("/admin/{id}")
    public ResponseEntity<?> getDocumentForAdmin(@PathVariable Long id) {
        // Gọi Service để lấy Entity thô từ Database
        Document doc = documentService.getDocumentById(id);

        // Dùng DocumentAdminResponse (không gộp ngôn ngữ) để trả về
        return ResponseEntity.ok(ApiResponse.success(
                DocumentAdminResponse.fromEntity(doc),
                "Lấy chi tiết tài liệu thành công"
        ));
    }


    // API Cập nhật thông tin tài liệu (Dành cho Web Admin)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDocument(
            @PathVariable Long id,
            @RequestBody DocumentRequest request,
            @RequestHeader(name = "Accept-Language", defaultValue = "vi") String lang) {

        Document doc = documentService.updateDocument(id, request);
        return ResponseEntity.ok(ApiResponse.success(DocumentResponse.fromEntity(doc, lang), "Cập nhật thành công"));
    }

    // API Xóa tài liệu (Dành cho Web Admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa tài liệu thành công"));
    }

    // API Download/View File - Giữ lại để tương thích ngược với các file cũ đã lưu trên disk (trước khi migrate sang Firebase)
    // File mới đã được lưu trên Firebase và có thể truy cập trực tiếp qua Firebase URL.
    @GetMapping("/files/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        try {
            Path filePath = Paths.get("uploads").resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                String contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
                if (contentType == null) contentType = "application/pdf";

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}