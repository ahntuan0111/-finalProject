package vn.anhtuan.finalProject.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GeminiValidationServiceTest {

    @InjectMocks
    private GeminiValidationService geminiValidationService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Since the constructor manually instantiates them, we inject mocks using Reflection
        ReflectionTestUtils.setField(geminiValidationService, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(geminiValidationService, "objectMapper", objectMapper);
        ReflectionTestUtils.setField(geminiValidationService, "geminiApiKey", "fake-key");
    }

    @Test
    void testValidateHandwriting_Success() throws Exception {
        String mockResponseBody = "{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"{\\\"isValid\\\": true, \\\"reason\\\": \\\"OK\\\"}\"}]}}]}";
        ResponseEntity<String> responseEntity = ResponseEntity.ok(mockResponseBody);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        JsonNode rootNode = new ObjectMapper().readTree(mockResponseBody);
        when(objectMapper.readTree(anyString())).thenReturn(rootNode);

        GeminiValidationService.GeminiValidationResult result = geminiValidationService.validateHandwriting("base64", "content", "vi");

        // The parseGeminiResponse will call objectMapper.readTree(aiText) again
        // We need to mock that too
        JsonNode aiJson = new ObjectMapper().readTree("{\"isValid\": true, \"reason\": \"OK\"}");
        when(objectMapper.readTree("{\"isValid\": true, \"reason\": \"OK\"}")).thenReturn(aiJson);

        assertNotNull(result);
        assertTrue(result.isValid);
        assertEquals("OK", result.aiReason);
    }
}
