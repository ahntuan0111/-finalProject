package vn.anhtuan.finalProject.dto.request;

import lombok.Data;

@Data
public class SaveSubmissionRequest {
    private String firebaseImageUrl;
    private String aiReason; // Optional: To save what AI said

    public SaveSubmissionRequest() {
    }

    public SaveSubmissionRequest(String firebaseImageUrl, String aiReason) {
        this.firebaseImageUrl = firebaseImageUrl;
        this.aiReason = aiReason;
    }

    public String getAiReason() {
        return aiReason;
    }

    public void setAiReason(String aiReason) {
        this.aiReason = aiReason;
    }

    public String getFirebaseImageUrl() {
        return firebaseImageUrl;
    }

    public void setFirebaseImageUrl(String firebaseImageUrl) {
        this.firebaseImageUrl = firebaseImageUrl;
    }
}
