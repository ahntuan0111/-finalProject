package vn.anhtuan.finalProject.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Cấu hình Jackson để xử lý:
 * 1. Hibernate Lazy Proxy (ByteBuddyInterceptor) - bỏ qua hibernateLazyInitializer
 * 2. Java 8 Time API (LocalDateTime, LocalDate) - serialize/deserialize đúng
 * 3. FAIL_ON_UNKNOWN_PROPERTIES = false để tránh lỗi khi nhận JSON từ client
 */
@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Fix #1: Xử lý Java 8 Time (LocalDateTime, LocalDate, etc.)
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Fix #2: Hibernate Lazy Proxy - FAIL_ON_EMPTY_BEANS = false
        // Khi Jackson gặp Hibernate proxy chưa được khởi tạo, thay vì throw
        // exception thì serialize ra {} (empty object) hoặc bỏ qua
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        // Fix #3: Bỏ qua các field không biết khi deserialize từ client
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        return mapper;
    }
}
