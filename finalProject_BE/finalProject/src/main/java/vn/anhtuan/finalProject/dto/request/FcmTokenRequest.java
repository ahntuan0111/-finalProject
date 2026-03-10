package vn.anhtuan.finalProject.dto.request;
import lombok.Data;

@Data
public class FcmTokenRequest {
    private String fcmToken;

    public FcmTokenRequest() {
    }

    public FcmTokenRequest(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
