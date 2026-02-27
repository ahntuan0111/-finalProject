package vn.anhtuan.finalProject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import vn.anhtuan.finalProject.security.JwtAuthenticationFilter;

@Configuration
@EnableMethodSecurity // Cho phép dùng @PreAuthorize("hasRole('ADMIN')") ở Controller
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Tắt CSRF (Vì dùng Token JWT nên không cần check CSRF session)
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Kích hoạt CORS (Rất quan trọng cho Web/Android)
                // Nó sẽ tìm Bean 'corsConfigurationSource' (thường khai báo ở file CorsConfig.java)
                .cors(Customizer.withDefaults())

                // 3. Phân quyền Request
                .authorizeHttpRequests((authorize) -> authorize
                        // Các API công khai (Không cần Token)
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/api/documents/files/**").permitAll()
                        // Swagger UI (Mở để test API dễ dàng)
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // Tất cả request còn lại bắt buộc phải có Token hợp lệ
                        .anyRequest().authenticated()
                )

                // 4. Xử lý lỗi trả về khi không có quyền (401/403)
                // Bạn có thể thêm class JwtAuthEntryPoint để custom JSON lỗi đẹp hơn (Optional)
                // .exceptionHandling(ex -> ex.authenticationEntryPoint(new JwtAuthEntryPoint()))

                // 5. Cấu hình Session là STATELESS (Không lưu session phía server)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 6. Thêm Filter kiểm tra JWT trước filter mặc định của Spring Security
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}