package vn.anhtuan.finalProject.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

public class ApiResponse<T> {
    private int code;      // <--- 1. Thêm trường code
    private boolean success;
    private String message;
    private T data;

    // 2. Cập nhật hàm success (mặc định code 200)
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(200, true, message, data);
    }

    // 3. Cập nhật hàm error để nhận thêm code
    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, false, message, null);
    }

    public ApiResponse() {
    }

    public ApiResponse(int code, boolean success, String message, T data) {
        this.code = code;
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}