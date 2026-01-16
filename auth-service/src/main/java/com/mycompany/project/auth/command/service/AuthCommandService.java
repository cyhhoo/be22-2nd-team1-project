package com.mycompany.project.auth.command.service;

import com.mycompany.project.auth.client.UserClient;
import com.mycompany.project.common.dto.UserInternalActivateRequest;
import com.mycompany.project.common.dto.UserInternalResponse;
import com.mycompany.project.auth.command.domain.aggregate.Token;
import com.mycompany.project.auth.command.domain.repository.TokenRepository;
import com.mycompany.project.auth.command.dto.AccountActivationRequest;
import com.mycompany.project.auth.query.dto.TokenResponse;
import com.mycompany.project.common.enums.UserStatus;
import com.mycompany.project.exception.BusinessException;
import com.mycompany.project.exception.ErrorCode;
import com.mycompany.project.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthCommandService {

    private final UserClient userClient;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenRepository tokenRepository;

    @Transactional
    public TokenResponse activateAccount(AccountActivationRequest request) {
        // 1. Get user (Feign)
        UserInternalResponse user = userClient.getByEmail(request.getEmail());
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 2. If already activated
        if (user.getStatus() == UserStatus.ACTIVE) {
            throw new IllegalArgumentException("Account is already activated.");
        }

        // 3. Identity verification
        if (!(user.getName().equals(request.getName()) && user.getBirthDate().equals(request.getBirthDate()))) {
            throw new IllegalArgumentException("User information does not match.");
        }

        // Auth code check
        if (user.getAuthCode() != null && !user.getAuthCode().equals(request.getAuthCode())) {
            throw new IllegalArgumentException("Auth code does not match.");
        }

        // 4. Activate (Feign call to update user-service data)
        String encryptedPassword = passwordEncoder.encode(request.getNewPassword());
        userClient.activateUser(new UserInternalActivateRequest(request.getEmail(), encryptedPassword));

        // Issue tokens immediately after activation
        String accessToken = jwtTokenProvider.createAccessToken(
                user.getUserId(), user.getEmail(), user.getRole(), UserStatus.ACTIVE);
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

        // Save refresh token to DB
        Token tokenEntity = Token.builder()
                .token(refreshToken)
                .email(user.getEmail())
                .build();
        java.util.Objects.requireNonNull(tokenEntity);
        tokenRepository.save(tokenEntity);

        return new TokenResponse(accessToken, refreshToken);
    }
}
