package vn.anhtuan.finalProject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.anhtuan.finalProject.dto.response.ApiResponse;
import vn.anhtuan.finalProject.service.FirebaseStorageService;

import java.io.IOException;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Autowired
    private FirebaseStorageService firebaseStorageService;

    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // Upload trực tiếp lên Firebase thư mục "web_admin_images"
            String fileUrl = firebaseStorageService.uploadImageToFirebase(file, "web_admin_images");

            return ResponseEntity.ok(ApiResponse.success(fileUrl, "Upload thành công"));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error(500, "Lỗi upload ảnh lên Firebase: " + e.getMessage()));
        }
    }
}