package vn.anhtuan.finalProject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.anhtuan.finalProject.dto.request.AiChatRequest;
import vn.anhtuan.finalProject.dto.response.ApiResponse;
import vn.anhtuan.finalProject.service.AiTutorService;

@RestController
@RequestMapping("/api/ai/chat")
public class AiTutorController {

    @Autowired
    private AiTutorService aiTutorService;

    /**
     * Gửi tin nhắn và nhận phản hồi từ AI Gia sư
     * POST /api/ai/chat
     */
    @PostMapping
    public ResponseEntity<?> chat(@RequestBody AiChatRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                aiTutorService.chat(request.getMessage()),
                "AI đã phản hồi thành công"
        ));
    }

    /**
     * Lấy lịch sử hội thoại của User hiện tại (session gần nhất)
     * GET /api/ai/chat/history
     */
    @GetMapping("/history")
    public ResponseEntity<?> getHistory() {
        return ResponseEntity.ok(ApiResponse.success(
                aiTutorService.getHistory(),
                "Lấy lịch sử thành công"
        ));
    }

    /**
     * Xoá toàn bộ lịch sử và bắt đầu cuộc trò chuyện mới
     * DELETE /api/ai/chat/history
     */
    @DeleteMapping("/history")
    public ResponseEntity<?> clearHistory() {
        aiTutorService.clearHistory();
        return ResponseEntity.ok(ApiResponse.success(null, "Đã xoá lịch sử hội thoại thành công"));
    }
}
