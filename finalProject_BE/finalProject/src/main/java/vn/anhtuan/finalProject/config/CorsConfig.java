package vn.anhtuan.finalProject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // 1. Cho phép Credentials (Cookie/Auth) - Quan trọng
        config.setAllowCredentials(true);

        // 2. Cho phép nguồn (Frontend URL)
        // Lưu ý: Không được dùng "*" nếu setAllowCredentials(true)
        // Hãy điền chính xác địa chỉ Web Admin của bạn
        config.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:3000"));

        // 3. Cho phép các Header
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));

        // 4. Cho phép các Method
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}