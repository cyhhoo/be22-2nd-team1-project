package com.mycompany.project.auth.query.service;

import com.mycompany.project.auth.client.UserClient;
import com.mycompany.project.auth.client.dto.UserAuthResponse;
import com.mycompany.project.auth.command.domain.aggregate.Token;
import com.mycompany.project.auth.command.domain.repository.TokenRepository;
import com.mycompany.project.auth.query.dto.LoginRequest;
import com.mycompany.project.auth.query.dto.TokenResponse;
import com.mycompany.project.common.enums.Role;
import com.mycompany.project.common.enums.UserStatus;
import com.mycompany.project.exception.BusinessException;
import com.mycompany.project.exception.ErrorCode;
import com.mycompany.project.security.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthQueryServiceTest {

    @InjectMocks
    private AuthQueryService authQueryService;

    @Mock
    private UserClient userClient;
    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("Login Success")
    void login_Success() {
        // given
        LoginRequest request = new LoginRequest("test@test.com", "password");
        UserAuthResponse mockUser = new UserAuthResponse();
        mockUser.setUserId(1L);
        mockUser.setEmail("test@test.com");
        mockUser.setPassword("encodedPassword");
        mockUser.setRole(Role.STUDENT);
        mockUser.setStatus(UserStatus.ACTIVE);
        mockUser.setLocked(false);

        given(userClient.getUserForAuth(request.getEmail())).willReturn(mockUser);
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
        verify(userClient).recordLoginSuccess(mockUser.getEmail());
    }

    @Test
    @DisplayName("Login Fail - Account Not Found")
    void login_AccountNotFound() {
        // given
        LoginRequest request = new LoginRequest("unknown@test.com", "password");
        given(userClient.getUserForAuth(request.getEmail())).willReturn(null);

        // when & then
        BusinessException ex = assertThrows(BusinessException.class, () -> authQueryService.login(request));
        assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    @DisplayName("Login Fail - Invalid Password")
    void login_InvalidPassword() {
        // given
        LoginRequest request = new LoginRequest("test@test.com", "wrongPass");
        UserAuthResponse mockUser = new UserAuthResponse();
        mockUser.setEmail("test@test.com");
        mockUser.setPassword("encodedPassword");
        mockUser.setStatus(UserStatus.ACTIVE);
        mockUser.setLocked(false);

        given(userClient.getUserForAuth(request.getEmail())).willReturn(mockUser);
        given(passwordEncoder.matches(request.getPassword(), mockUser.getPassword())).willReturn(false);

        // when & then
        BusinessException ex = assertThrows(BusinessException.class, () -> authQueryService.login(request));
        assertEquals(ErrorCode.INVALID_PASSWORD, ex.getErrorCode());

        verify(tokenRepository, never()).save(any(Token.class));
        verify(userClient).recordLoginFail(mockUser.getEmail());
    }

    @Test
    @DisplayName("Login Fail - Account Locked")
    void login_AccountLocked() {
        // given
        LoginRequest request = new LoginRequest("locked@test.com", "password");
        UserAuthResponse mockUser = new UserAuthResponse();
        mockUser.setEmail("locked@test.com");
        mockUser.setStatus(UserStatus.ACTIVE);
        mockUser.setLocked(true);

        given(userClient.getUserForAuth(request.getEmail())).willReturn(mockUser);

        // when & then
        BusinessException ex = assertThrows(BusinessException.class, () -> authQueryService.login(request));
        assertEquals(ErrorCode.ACCOUNT_LOCKED, ex.getErrorCode());
    }
}
