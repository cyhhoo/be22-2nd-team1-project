package com.mycompany.project.auth.command.service;

import com.mycompany.project.auth.client.UserClient;
import com.mycompany.project.auth.client.dto.UserInternalActivateRequest;
import com.mycompany.project.auth.client.dto.UserResponse;
import com.mycompany.project.auth.command.domain.aggregate.Token;
import com.mycompany.project.auth.command.domain.repository.TokenRepository;
import com.mycompany.project.auth.command.dto.AccountActivationRequest;
import com.mycompany.project.auth.query.dto.TokenResponse;
import com.mycompany.project.common.enums.Role;
import com.mycompany.project.common.enums.UserStatus;
import com.mycompany.project.exception.BusinessException;
import com.mycompany.project.security.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthCommandServiceTest {

    @InjectMocks
    private AuthCommandService authCommandService;

    @Mock
    private UserClient userClient;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private TokenRepository tokenRepository;

    @Test
    @DisplayName("Account Activation Success")
    void activateAccount_Success() {
        // given
        AccountActivationRequest request = new AccountActivationRequest(
                "test@test.com", "Hong Gil Dong", LocalDate.of(2000, 1, 1), "123456", "newPassword");

        UserResponse mockUser = new UserResponse();
        mockUser.setUserId(1L);
        mockUser.setEmail("test@test.com");
        mockUser.setName("Hong Gil Dong");
        mockUser.setBirthDate(LocalDate.of(2000, 1, 1));
        mockUser.setAuthCode("123456");
        mockUser.setStatus(UserStatus.INACTIVE);
        mockUser.setRole(Role.STUDENT);

        given(userClient.getByEmail(request.getEmail())).willReturn(mockUser);
        given(passwordEncoder.encode(request.getNewPassword())).willReturn("encodedPassword");
        given(jwtTokenProvider.createAccessToken(any(), any(), any(), any())).willReturn("accessToken");
        given(jwtTokenProvider.createRefreshToken(any())).willReturn("refreshToken");

        // when
        TokenResponse response = authCommandService.activateAccount(request);

        // then
        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());

        verify(userClient).activateUser(any(UserInternalActivateRequest.class));
        verify(tokenRepository).save(any(Token.class));
    }

    @Test
    @DisplayName("Account Activation Fail - User Not Found")
    void activateAccount_UserNotFound() {
        // given
        AccountActivationRequest request = new AccountActivationRequest(
                "unknown@test.com", "Unknown", LocalDate.now(), "000000", "pass");

        given(userClient.getByEmail(request.getEmail())).willReturn(null);

        // when & then
        assertThrows(BusinessException.class, () -> authCommandService.activateAccount(request));
    }

    @Test
    @DisplayName("Account Activation Fail - Already Active")
    void activateAccount_AlreadyActive() {
        // given
        AccountActivationRequest request = new AccountActivationRequest(
                "active@test.com", "Hong Gil Dong", LocalDate.of(2000, 1, 1), "123456", "pass");

        UserResponse mockUser = new UserResponse();
        mockUser.setStatus(UserStatus.ACTIVE);

        given(userClient.getByEmail(request.getEmail())).willReturn(mockUser);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> authCommandService.activateAccount(request));
    }

    @Test
    @DisplayName("Account Activation Fail - Info Mismatch (Name)")
    void activateAccount_InfoMismatch() {
        // given
        AccountActivationRequest request = new AccountActivationRequest(
                "test@test.com", "Different Name", LocalDate.of(2000, 1, 1), "123456", "pass");

        UserResponse mockUser = new UserResponse();
        mockUser.setName("Hong Gil Dong");
        mockUser.setBirthDate(LocalDate.of(2000, 1, 1));
        mockUser.setStatus(UserStatus.INACTIVE);

        given(userClient.getByEmail(request.getEmail())).willReturn(mockUser);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> authCommandService.activateAccount(request));
    }
}
