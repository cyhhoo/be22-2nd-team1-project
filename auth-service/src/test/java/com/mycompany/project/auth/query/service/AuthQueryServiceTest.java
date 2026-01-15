package com.mycompany.project.auth.query.service;

import com.mycompany.project.auth.query.dto.LoginRequest;
import com.mycompany.project.auth.query.dto.TokenResponse;
import com.mycompany.project.exception.BusinessException;
import com.mycompany.project.exception.ErrorCode;
import com.mycompany.project.security.JwtTokenProvider;
import com.mycompany.project.user.command.domain.aggregate.Role;
import com.mycompany.project.user.command.domain.aggregate.Token;
import com.mycompany.project.user.command.domain.aggregate.User;
import com.mycompany.project.user.command.domain.aggregate.UserStatus;
import com.mycompany.project.user.command.domain.repository.TokenRepository;
import com.mycompany.project.user.command.domain.repository.UserRepository;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

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

    @Test
    @DisplayName("로그인 성공 - 정상 케이스")
    void login_Success() {
        // given
        LoginRequest request = new LoginRequest("test@test.com", "password");
        User mockUser = User.builder()
                .userId(1L)
                .email("test@test.com")
                .password("encodedPassword")
                .role(Role.STUDENT)
                .status(UserStatus.ACTIVE)
                .build();

        given(userRepository.findByEmail(request.getEmail())).willReturn(Optional.of(mockUser));
        given(passwordEncoder.matches(request.getPassword(), mockUser.getPassword())).willReturn(true);
        given(jwtTokenProvider.createAccessToken(any(), any(), any(), any())).willReturn("accessToken");
        given(jwtTokenProvider.createRefreshToken(any())).willReturn("refreshToken");

        // when
        TokenResponse response = authQueryService.login(request);

        // then
        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
        verify(tokenRepository).save(any(Token.class));
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 계정")
    void login_AccountNotFound() {
        // given
        LoginRequest request = new LoginRequest("unknown@test.com", "password");
        given(userRepository.findByEmail(request.getEmail())).willReturn(Optional.empty());

        // when & then
        BusinessException ex = assertThrows(BusinessException.class, () -> authQueryService.login(request));
        assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void login_InvalidPassword() {
        // given
        LoginRequest request = new LoginRequest("test@test.com", "wrongPass");
        User mockUser = User.builder()
                .email("test@test.com")
                .password("encodedPassword")
                .status(UserStatus.ACTIVE)
                .build();

        given(userRepository.findByEmail(request.getEmail())).willReturn(Optional.of(mockUser));
        given(passwordEncoder.matches(request.getPassword(), mockUser.getPassword())).willReturn(false);

        // when & then
        BusinessException ex = assertThrows(BusinessException.class, () -> authQueryService.login(request));
        assertEquals(ErrorCode.INVALID_PASSWORD, ex.getErrorCode());

        // save token should not be called
        verify(tokenRepository, never()).save(any(Token.class));
    }

    @Test
    @DisplayName("로그인 실패 - 계정 잠금")
    void login_AccountLocked() {
        // given
        LoginRequest request = new LoginRequest("locked@test.com", "password");
        User mockUser = User.builder()
                .email("locked@test.com")
                .status(UserStatus.LOCKED) // Locked status
                .build();

        given(userRepository.findByEmail(request.getEmail())).willReturn(Optional.of(mockUser));

        // when & then
        BusinessException ex = assertThrows(BusinessException.class, () -> authQueryService.login(request));
        assertEquals(ErrorCode.ACCOUNT_LOCKED, ex.getErrorCode());
    }
}
