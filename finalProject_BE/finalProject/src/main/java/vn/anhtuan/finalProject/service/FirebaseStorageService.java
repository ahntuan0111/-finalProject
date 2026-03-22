package vn.anhtuan.finalProject.service;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.HttpMethod;
import com.google.cloud.storage.Storage;
import com.google.firebase.cloud.StorageClient;
import org.springframework.stereotype.Service;

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
}
