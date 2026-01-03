package vn.anhtuan.finalProject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.anhtuan.finalProject.dto.request.PasswordChangeRequest;
import vn.anhtuan.finalProject.dto.request.UpdateGradeRequest;
import vn.anhtuan.finalProject.dto.response.ApiResponse;
import vn.anhtuan.finalProject.entity.User;
import vn.anhtuan.finalProject.service.IUserService;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    // API: PUT /api/users/change-password
    // Yêu cầu: Header Authorization: Bearer <token>
    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(@Valid @RequestBody PasswordChangeRequest request) {
        userService.changePassword(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Đổi mật khẩu thành công"));
    }

    @PostMapping("/update-grade")
    public ResponseEntity<ApiResponse<String>> updateGrade(
            @RequestBody UpdateGradeRequest request,
            Authentication auth
    ) {
        // --- SỬA ĐOẠN NÀY ---
        // Sai: String username = auth.getName(); // Nó đang trả về toString() của object

        // Đúng: Ép kiểu về User Entity để lấy username chính xác
        User userPrincipal = (User) auth.getPrincipal();
        String username = userPrincipal.getUsername();
        // --------------------

        logger.info(">>> API REQUEST: /update-grade");
        logger.info(">>> User: {}", username); // Lúc này nó sẽ in ra "anhtuan0111"
        logger.info(">>> Payload Grade: {}", request.getGrade());

        try {
            userService.updateGrade(username, request.getGrade());

            logger.info("<<< UPDATE GRADE SUCCESS for user: {}", username);
            return ResponseEntity.ok(ApiResponse.success("Cập nhật thành công", "Thành công"));
        } catch (Exception e) {
            logger.error("!!! UPDATE GRADE FAILED: {}", e.getMessage());
            throw e;
        }
    }

    @GetMapping("/current-grade")
    public ResponseEntity<ApiResponse<Integer>> getCurrentGrade(Authentication auth) {
        // --- SỬA TƯƠNG TỰ ---
        User userPrincipal = (User) auth.getPrincipal();
        String username = userPrincipal.getUsername();
        // --------------------

        logger.info(">>> API REQUEST: /current-grade | User: {}", username);

        Integer grade = userService.getCurrentGrade(username);

        logger.info("<<< CURRENT GRADE RESPONSE: {}", grade);
        return ResponseEntity.ok(ApiResponse.success(grade, "Thành công"));
    }
}