package vn.anhtuan.finalProject.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.anhtuan.finalProject.dto.response.ApiResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    // Thư mục lưu ảnh (Bạn có thể cấu hình trong application.properties)
    private static final String UPLOAD_DIR = "uploads/";

    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // 1. Tạo tên file unique để tránh trùng
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

            // 2. Tạo thư mục nếu chưa có
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 3. Lưu file vào ổ cứng server
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 4. Trả về URL (Lưu ý: Cần cấu hình ResourceHandler để truy cập được file này qua HTTP)
            // Ví dụ trả về: http://localhost:8080/uploads/ten_file.jpg
            // Tạm thời trả về đường dẫn tương đối để FE hiển thị
            String fileUrl = "/uploads/" + fileName;

            return ResponseEntity.ok(ApiResponse.success(fileUrl, "Upload thành công"));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error(500, "Lỗi upload ảnh: " + e.getMessage()));
        }
    }
}