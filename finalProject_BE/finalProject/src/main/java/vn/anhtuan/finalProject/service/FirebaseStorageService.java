package vn.anhtuan.finalProject.service;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.HttpMethod;
import com.google.cloud.storage.Storage;
import com.google.firebase.cloud.StorageClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class FirebaseStorageService {

    /**
     * Tạo Signed URL cho phép Mobile upload ảnh trực tiếp lên Firebase Storage.
     * URL có thời hạn 5 phút và chỉ cho phép PUT (upload).
     */
    public Map<String, String> generateSignedUploadUrl(Long exerciseId) {
        try {
            Storage storage = StorageClient.getInstance().bucket().getStorage();
            String bucketName = StorageClient.getInstance().bucket().getName();

            // Tạo tên file duy nhất
            String fileName = "submissions/" + exerciseId + "/" + UUID.randomUUID() + ".jpg";

            BlobId blobId = BlobId.of(bucketName, fileName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType("image/jpeg")
                    .build();

            // Tạo Signed URL với thời hạn 5 phút
            URL signedUrl = storage.signUrl(
                    blobInfo,
                    5,
                    TimeUnit.MINUTES,
                    Storage.SignUrlOption.httpMethod(HttpMethod.PUT),
                    Storage.SignUrlOption.withContentType()
            );

            // Tạo public download URL
            String publicUrl = String.format(
                    "https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                    bucketName,
                    fileName.replace("/", "%2F")
            );

            Map<String, String> result = new HashMap<>();
            result.put("uploadUrl", signedUrl.toString());
            result.put("fileName", fileName);
            result.put("publicUrl", publicUrl);

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi tạo Signed URL: " + e.getMessage());
        }
    }

    /**
     * Upload trực tiếp file PDF lên Firebase Storage (dành riêng cho tài liệu)
     */
    public String uploadPdfToFirebase(MultipartFile file) throws IOException {
        Storage storage = StorageClient.getInstance().bucket().getStorage();
        String bucketName = StorageClient.getInstance().bucket().getName();

        // Tạo tên file an toàn, prefix theo folder documents/
        String originalFilename = file.getOriginalFilename() != null
                ? file.getOriginalFilename().replaceAll("[^a-zA-Z0-9.-]", "_")
                : "document.pdf";
        String fileName = "documents/" + UUID.randomUUID().toString() + "_" + originalFilename;

        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType("application/pdf")
                .build();

        // Tải file lên Firebase
        storage.create(blobInfo, file.getBytes());

        // Trả về URL public
        return String.format(
                "https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                bucketName,
                fileName.replace("/", "%2F")
        );
    }

    /**
     * Xóa file trên Firebase Storage theo public URL
     */
    public void deleteFileFromFirebase(String publicUrl) {
        try {
            if (publicUrl == null || !publicUrl.contains("firebasestorage.googleapis.com")) return;

            Storage storage = StorageClient.getInstance().bucket().getStorage();
            String bucketName = StorageClient.getInstance().bucket().getName();

            // Tách fileName từ URL: /o/documents%2F.../o?alt=media
            String encodedPath = publicUrl.split("/o/")[1].split("\\?")[0];
            String filePath = java.net.URLDecoder.decode(encodedPath, "UTF-8");

            BlobId blobId = BlobId.of(bucketName, filePath);
            storage.delete(blobId);
        } catch (Exception e) {
            System.err.println("Không thể xóa file Firebase: " + e.getMessage());
        }
    }

    /**
     * Upload trực tiếp file lên Firebase Storage (sử dụng cho Web-Admin)
     */
    public String uploadImageToFirebase(MultipartFile file, String folderName) throws IOException {
        Storage storage = StorageClient.getInstance().bucket().getStorage();
        String bucketName = StorageClient.getInstance().bucket().getName();

        // Tạo tên file an toàn
        String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename().replaceAll("[^a-zA-Z0-9.-]", "_") : "image.jpg";
        String fileName = folderName + "/" + UUID.randomUUID().toString() + "_" + originalFilename;

        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .build();

        // Tải file lên Firebase
        storage.create(blobInfo, file.getBytes());

        // Trả về URL public truy cập ảnh
        return String.format(
                "https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                bucketName,
                fileName.replace("/", "%2F")
        );
    }
}
