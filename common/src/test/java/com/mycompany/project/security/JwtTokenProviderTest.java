package com.mycompany.project.security;

import com.mycompany.project.user.command.domain.aggregate.Role;
import com.mycompany.project.user.command.domain.aggregate.UserStatus;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private final String secret = "mysecretkeymysecretkeymysecretkeymysecretkey"; // 32+ chars

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "secretKey", secret);
        jwtTokenProvider.init();
    }

    @Test
    @DisplayName("토큰 생성 및 유효성 검증 성공")
    void createAndValidateToken_Success() {
        // given
        Long userId = 1L;
        String email = "test@test.com";
        Role role = Role.STUDENT;
        UserStatus status = UserStatus.ACTIVE;

        // when
        String token = jwtTokenProvider.createAccessToken(userId, email, role, status);

        // then
        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
    }

    @Test
    @DisplayName("만료된 토큰 검증 실패")
    void validateToken_Expired() {
        // given - Manually create an expired token
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        String expiredToken = Jwts.builder()
                .subject("test@test.com")
                .signWith(key)
                .expiration(new java.util.Date(System.currentTimeMillis() - 1000)) // Past time
                .compact();

        // when
        boolean isValid = jwtTokenProvider.validateToken(expiredToken);

        // then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("토큰에서 상태값 추출 테스트")
    void getStatusFromToken_Success() {
        // given
        String token = jwtTokenProvider.createAccessToken(1L, "test@test.com", Role.ADMIN, UserStatus.INACTIVE);

        // when
        String status = jwtTokenProvider.getStatusFromToken(token);

        // then
        assertEquals("INACTIVE", status);
    }
}
