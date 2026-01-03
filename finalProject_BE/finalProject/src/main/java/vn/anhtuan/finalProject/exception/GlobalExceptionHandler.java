package vn.anhtuan.finalProject.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import vn.anhtuan.finalProject.dto.response.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Xử lý các lỗi Runtime chưa được bắt cụ thể
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleUnwantedException(Exception e) {
        // QUAN TRỌNG: In toàn bộ Stack Trace ra Console của IntelliJ
        logger.error("UNKNOWN ERROR: ", e);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lỗi hệ thống: " + e.getMessage()));
    }

}