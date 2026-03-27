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
        String prompt = "Bạn là một giáo viên chấm thi nghiêm khắc. Câu hỏi: [" + exerciseContent + "]. "
                + "Đây là ảnh bài tập viết tay của học sinh. Hãy kiểm tra theo CÁC TIÊU CHÍ SAU ĐÂY:\n"
                + "TIÊU CHÍ 1 – Nội dung hợp lệ: Ảnh phải chứa nội dung RÕ RÀNG liên quan đến việc giải câu hỏi trên (công thức toán, phép tính, hình vẽ, sơ đồ). "
                + "Không chấp nhận: giấy trắng, phong cảnh, selfie, thú cưng, đồ vật, chữ lan man không liên quan.\n"
                + "TIÊU CHÍ 2 – Diện tích hợp lệ: Bài giải phải CHIẾM ÍT NHẤT 20% diện tích ảnh. "
                + "Nếu nội dung chỉ xuất hiện ở một góc nhỏ, bị cắt xén, mờ hoặc không đọc được -> TỪ CHỐI, yêu cầu học sinh chụp lại cho đầy đủ và rõ ràng hơn.\n"
                + "TIÊU CHÍ 3 – Không gian trống: Nếu hơn 80% diện tích ảnh là giấy trắng hoặc nền trống, dù góc ảnh có chút chữ -> TỪ CHỐI.\n"
                + "TIÊU CHÍ 4 – Đúng câu hỏi (QUAN TRỌNG NHẤT – chống gian lận): Đọc kỹ nội dung bài giải trong ảnh. "
                + "Xác minh xem bài giải đó CÓ ĐANG CỐ GẮNG GIẢI QUYẾT ĐÚNG CÂU HỎI ĐÃ CHO không. "
                + "Ví dụ: nếu câu hỏi là (-7) - (-5/8), nhưng ảnh lại là bài giải phương trình bậc 2 hoặc một phép tính hoàn toàn khác -> TỪ CHỐI. "
                + "Không cần đáp án phải đúng, nhưng VẤN ĐỀ ĐANG GIẢI PHẢI TRÙNG KHỚP với câu hỏi đã cho.\n"
                + "Chỉ xuất ra JSON. Định dạng bắt buộc: {\"isValid\": true hoặc false, \"reason\": \"Lý do ngắn gọn bằng tiếng Việt\"}";

        if ("en".equalsIgnoreCase(lang)) {
            prompt = "You are a strict grading teacher. Question: [" + exerciseContent + "]. "
                + "This is the student's handwritten answer photo. Check ALL the following criteria:\n"
                + "CRITERIA 1 – Valid content: The image must CLEARLY contain content related to solving the question (math formulas, calculations, diagrams). "
                + "Not acceptable: blank paper, scenery, selfie, pets, objects, or unrelated random text.\n"
                + "CRITERIA 2 – Sufficient area: The handwritten work must occupy AT LEAST 20% of the image area. "
                + "If content only appears in a tiny corner, is cut off, blurry or unreadable -> REJECT and ask student to retake.\n"
                + "CRITERIA 3 – Whitespace: If more than 80% of the image is blank/white, even if there is a little writing in a corner -> REJECT.\n"
                + "CRITERIA 4 – Correct question (MOST IMPORTANT – anti-cheating): Read the solution in the image carefully. "
                + "Verify that the solution IS ATTEMPTING TO SOLVE THE SPECIFIC GIVEN QUESTION. "
                + "Example: if the question is (-7) - (-5/8) but the image shows a quadratic equation or a completely different calculation -> REJECT. "
                + "The answer doesn't have to be correct, but THE PROBLEM BEING SOLVED MUST MATCH the given question.\n"
                + "Output ONLY JSON. Strict format: {\"isValid\": true or false, \"reason\": \"Short reason in English\"}";
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
