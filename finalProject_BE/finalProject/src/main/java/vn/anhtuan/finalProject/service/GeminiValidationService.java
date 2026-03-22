package vn.anhtuan.finalProject.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiValidationService {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GeminiValidationService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public static class GeminiValidationResult {
        public boolean isValid;
        public String aiReason;
    }

    public GeminiValidationResult validateHandwriting(String base64Image, String exerciseContent, String lang) {
        String prompt = "Bạn là một giáo viên chấm thi. Đây là câu hỏi: [" + exerciseContent + "]. "
                + "Đây là bức ảnh bài tập viết tay của học sinh (được base64 encode). "
                + "Bạn KHÔNG cần chấm điểm đúng/sai. Hãy đánh giá xem bức ảnh này CÓ CHỨA NỘI DUNG LIÊN QUAN ĐẾN VIỆC GIẢI CÂU HỎI TRÊN KHÔNG (Ví dụ: có viết công thức toán, có các phép tính, hình vẽ, hay dòng chữ liên quan). "
                + "Nếu ảnh chụp cảnh vật lung tung, đồ vật, selfie, thú cưng, giấy trắng, hoặc viết linh tinh chống đối -> KHÔNG HỢP LỆ. "
                + "Chỉ xuất ra JSON. Định dạng bắt buộc: {\"isValid\": true hoặc false, \"reason\": \"Lý do ngắn gọn\"}";
        
        if ("en".equalsIgnoreCase(lang)) {
            prompt = "You are a teacher grading a test. Question: [" + exerciseContent + "]. "
                + "Here is the picture of student's handwriting (base64 encoded). "
                + "You DO NOT need to check if the answer is correct or wrong. Only verify if the image CONTAINS CONTENTS RELATED TO SOLVING THE QUESTION (e.g. math formulas, calculations, related text). "
                + "If the image is scenery, objects, selfie, pets, blank paper, or random unrelated text -> NOT VALID. "
                + "Output ONLY JSON. Strict format: {\"isValid\": true or false, \"reason\": \"Short reason\"}";
        }

        try {
            Map<String, Object> textPart = new HashMap<>();
            textPart.put("text", prompt);

            Map<String, Object> inlineData = new HashMap<>();
            inlineData.put("mimeType", "image/jpeg");
            inlineData.put("data", base64Image); // Base64 without data:image/jpeg;base64, prefix

            Map<String, Object> imagePart = new HashMap<>();
            imagePart.put("inlineData", inlineData);

            Map<String, Object> parts = new HashMap<>();
            parts.put("parts", List.of(textPart, imagePart));

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("contents", List.of(parts));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            String url = GEMINI_API_URL + geminiApiKey;

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            
            return parseGeminiResponse(response.getBody());

        } catch (org.springframework.web.client.HttpClientErrorException.TooManyRequests e) {
            // 429 - Hết quota Gemini miễn phí
            e.printStackTrace();
            GeminiValidationResult errorResult = new GeminiValidationResult();
            errorResult.isValid = false;
            errorResult.aiReason = "Hệ thống AI đang quá tải (hết quota miễn phí). Vui lòng thử lại sau 1 phút.";
            return errorResult;
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            // Các lỗi HTTP khác từ Gemini (400, 404, v.v.)
            e.printStackTrace();
            GeminiValidationResult errorResult = new GeminiValidationResult();
            errorResult.isValid = false;
            errorResult.aiReason = "Lỗi từ hệ thống AI: " + e.getStatusCode().value() + ". Vui lòng thử lại sau.";
            return errorResult;
        } catch (Exception e) {
            e.printStackTrace();
            GeminiValidationResult errorResult = new GeminiValidationResult();
            errorResult.isValid = false;
            errorResult.aiReason = "Lỗi kết nối đến hệ thống kiểm duyệt AI: " + e.getMessage();
            return errorResult;
        }
    }

    private GeminiValidationResult parseGeminiResponse(String responseBody) {
        GeminiValidationResult result = new GeminiValidationResult();
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode candidates = rootNode.path("candidates");
            if (candidates.isArray() && candidates.size() > 0) {
                String aiText = candidates.get(0).path("content").path("parts").get(0).path("text").asText();
                
                // Tiền xử lý để bóc tách JSON (Loại bỏ ```json, ```...)
                aiText = aiText.replaceAll("```json", "").replaceAll("```", "").trim();
                
                JsonNode aiJson = objectMapper.readTree(aiText);
                result.isValid = aiJson.path("isValid").asBoolean(false);
                result.aiReason = aiJson.path("reason").asText("Không xác định");
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        result.isValid = false;
        result.aiReason = "Không thể đọc phản hồi từ AI";
        return result;
    }
}
