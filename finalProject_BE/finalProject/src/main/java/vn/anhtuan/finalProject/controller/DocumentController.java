package vn.anhtuan.finalProject.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.anhtuan.finalProject.dto.response.ApiResponse;
import vn.anhtuan.finalProject.entity.Document;
import vn.anhtuan.finalProject.service.DocumentService;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    // API Upload (Dành cho Web Admin)
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("subjectCode") String subjectCode,
            @RequestParam("grade") Integer grade,
            @RequestParam("examType") String examType) {

        Document doc = documentService.uploadDocument(file, title, description, subjectCode, grade, examType);
        return ResponseEntity.ok(ApiResponse.success(doc, "Upload thành công"));
    }

    // API Get List (Dành cho Android App)
    @GetMapping
    public ResponseEntity<?> getDocuments(
            @RequestParam("subjectCode") String subjectCode,
            @RequestParam("grade") Integer grade) {

        List<Document> docs = documentService.getDocuments(subjectCode, grade);
        return ResponseEntity.ok(ApiResponse.success(docs, "Lấy danh sách tài liệu thành công"));
    }

    // API Download/View File (Để App load PDF)
    @GetMapping("/files/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        try {
            Path filePath = Paths.get("uploads").resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if(resource.exists()) {
                String contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
                if(contentType == null) contentType = "application/octet-stream";

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

    // API Lấy tất cả tài liệu (Dành cho Web Admin)
    @GetMapping("/all")
    public ResponseEntity<?> getAllDocuments() {
        List<Document> docs = documentService.getAllDocuments();
        return ResponseEntity.ok(ApiResponse.success(docs, "Lấy danh sách tài liệu thành công"));
    }

    // API Cập nhật thông tin tài liệu (Dành cho Web Admin)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDocument(
            @PathVariable Long id,
            @RequestBody Document updatedDocInfo) {
        Document doc = documentService.updateDocument(id, updatedDocInfo);
        return ResponseEntity.ok(ApiResponse.success(doc, "Cập nhật thành công"));
    }

    // API Xóa tài liệu (Dành cho Web Admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa tài liệu thành công"));
    }
}