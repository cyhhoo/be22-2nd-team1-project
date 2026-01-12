package com.mycompany.project.auth.query.service;

import com.mycompany.project.auth.query.dto.LoginRequest;
import com.mycompany.project.auth.query.dto.TokenResponse;
import com.mycompany.project.exception.BusinessException;
import com.mycompany.project.jwtsecurity.JwtTokenProvider;
import com.mycompany.project.user.command.domain.aggregate.Role;
import com.mycompany.project.user.command.domain.aggregate.Token;
import com.mycompany.project.user.command.domain.aggregate.User;
import com.mycompany.project.user.command.domain.aggregate.UserStatus;
import com.mycompany.project.user.command.domain.repository.TokenRepository;
import com.mycompany.project.user.command.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthQueryServiceTest {

    @InjectMocks
    private AuthQueryService authQueryService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    private User activeUser;
    private User inactiveUser;

    @BeforeEach
    void setUp() {
        activeUser = User.builder()
                .email("test@example.com")
                .password("encodedPassword")
                .name("Tester")
                .role(Role.STUDENT)
                .status(UserStatus.ACTIVE)
                .loginFailCount(0)
                .build();

        inactiveUser = User.builder()
                .email("inactive@example.com")
                .password("encodedPassword")
                .name("Inactive")
                .role(Role.STUDENT)
                .status(UserStatus.INACTIVE)
                .build();
    }

    @Test
    @DisplayName("로그인 성공: 토큰 발급 및 저장")
    void login_Success() {
        // Given
        LoginRequest request = new LoginRequest("test@example.com", "password");
        given(userRepository.findByEmail(request.getEmail())).willReturn(Optional.of(activeUser));
        given(passwordEncoder.matches(request.getPassword(), activeUser.getPassword())).willReturn(true);
        given(jwtTokenProvider.createAccessToken(any(), any(), any())).willReturn("access-token");
        given(jwtTokenProvider.createRefreshToken(any())).willReturn("refresh-token");

        // When
        TokenResponse response = authQueryService.login(request);

        // Then
        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        verify(tokenRepository, times(1)).save(any(Token.class));
    }

    @Test
    @DisplayName("로그인 실패: 비밀번호 불일치")
    void login_Fail_PasswordMismatch() {
        // Given
        LoginRequest request = new LoginRequest("test@example.com", "wrongPassword");
        given(userRepository.findByEmail(request.getEmail())).willReturn(Optional.of(activeUser));
        given(passwordEncoder.matches(request.getPassword(), activeUser.getPassword())).willReturn(false);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> authQueryService.login(request));
        verify(tokenRepository, never()).save(any(Token.class));
    }

    @Test
    @DisplayName("로그인 실패: 활성화되지 않은 계정")
    void login_Fail_InactiveAccount() {
        // Given
        LoginRequest request = new LoginRequest("inactive@example.com", "password");
        given(userRepository.findByEmail(request.getEmail())).willReturn(Optional.of(inactiveUser));

        // When & Then
        assertThrows(BusinessException.class, () -> authQueryService.login(request));
    }

    @Test
    @DisplayName("토큰 재발급 성공 (RTR)")
    void reissue_Success() {
        // Given
        String oldRefreshToken = "old-refresh-token";
        Token storedToken = Token.builder().token(oldRefreshToken).email("test@example.com").build();

        given(jwtTokenProvider.validateToken(oldRefreshToken)).willReturn(true);
        given(tokenRepository.findByToken(oldRefreshToken)).willReturn(Optional.of(storedToken));
        given(userRepository.findByEmail(storedToken.getEmail())).willReturn(Optional.of(activeUser));
        given(jwtTokenProvider.createAccessToken(any(), any(), any())).willReturn("new-access-token");
        given(jwtTokenProvider.createRefreshToken(any())).willReturn("new-refresh-token");

        // When
        TokenResponse response = authQueryService.reissue(oldRefreshToken);

        // Then
        assertNotNull(response);
        assertEquals("new-access-token", response.getAccessToken());
        verify(tokenRepository, times(1)).delete(storedToken); // 기존 토큰 삭제 확인
        verify(tokenRepository, times(1)).save(any(Token.class)); // 새 토큰 저장 확인
    }

    @Test
    @DisplayName("로그아웃 성공: DB에서 토큰 삭제")
    void logout_Success() {
        // Given
        String refreshToken = "valid-refresh-token";
        String email = "test@example.com";

        given(jwtTokenProvider.validateToken(refreshToken)).willReturn(true);
        given(jwtTokenProvider.getUserEmailFromJWT(refreshToken)).willReturn(email);

        // When
        authQueryService.logout(refreshToken);

        // Then
        verify(tokenRepository, times(1)).deleteByEmail(email);
    }
}
