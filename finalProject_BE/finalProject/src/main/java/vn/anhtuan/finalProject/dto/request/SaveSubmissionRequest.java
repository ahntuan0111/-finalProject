package vn.anhtuan.finalProject.dto.request;

import lombok.Data;

@Data
public class SaveSubmissionRequest {
    private String firebaseImageUrl;
    private String aiReason; // Optional: To save what AI said
}
