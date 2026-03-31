package vn.anhtuan.finalProject.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import vn.anhtuan.finalProject.entity.Role;
import vn.anhtuan.finalProject.entity.User;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private final String secret = "9a4f2c8d3b7e1f6g5h4i3j2k1l0m9n8o7p6q5r4s3t2u1v0w9x8y7z6a5b4c3d2e";
    private final long expiration = 3600; // 1 hour

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", secret);
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpiration", expiration);
    }

    @Test
    void testGenerateToken_ShouldEmbedCorrectClaims() {
        User user = new User();
        user.setId(100L);
        user.setUsername("testuser");
        user.setEmail("test@gmail.com");
        user.setRole(Role.USER);

        String token = jwtTokenProvider.generateToken(user);

        assertThat(token).isNotNull();
        assertThat(jwtTokenProvider.getUserIdFromToken(token)).isEqualTo(100L);
    }

    @Test
    void testValidateToken_WithValidToken_ShouldReturnTrue() {
        User user = new User();
        user.setId(1L);
        user.setRole(Role.USER);
        String token = jwtTokenProvider.generateToken(user);

        boolean isValid = jwtTokenProvider.validateToken(token);

        assertThat(isValid).isTrue();
    }

    @Test
    void testValidateToken_WithInvalidToken_ShouldReturnFalse() {
        String invalidToken = "invalid.token.here";

        boolean isValid = jwtTokenProvider.validateToken(invalidToken);

        assertThat(isValid).isFalse();
    }
    
    @Test
    void testValidateToken_WithExpiredToken_ShouldReturnFalse() {
        // Mock a very short expiration
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpiration", -10L); 
        
        User user = new User();
        user.setId(1L);
        user.setRole(Role.USER);
        String token = jwtTokenProvider.generateToken(user);

        boolean isValid = jwtTokenProvider.validateToken(token);

        assertThat(isValid).isFalse();
    }
}
