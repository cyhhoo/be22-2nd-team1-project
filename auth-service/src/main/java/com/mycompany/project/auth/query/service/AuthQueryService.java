package com.mycompany.project.auth.query.service;

import com.mycompany.project.auth.client.UserClient;
import com.mycompany.project.common.dto.UserAuthResponse;
import com.mycompany.project.auth.command.domain.aggregate.Token;
import com.mycompany.project.auth.command.domain.repository.TokenRepository;
import com.mycompany.project.auth.query.dto.LoginRequest;
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
public class AuthQueryService {

    private final UserClient userClient;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    private TokenResponse generateAndSaveToken(UserAuthResponse user) {
        String accessToken = jwtTokenProvider.createAccessToken(user.getUserId(), user.getEmail(), user.getRole(),
                user.getStatus());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

        Token tokenEntity = Token.builder()
                .token(refreshToken)
                .email(user.getEmail())
                .build();
        tokenRepository.save(java.util.Objects.requireNonNull(tokenEntity));

        return new TokenResponse(accessToken, refreshToken);
    }

    /**
     * Login and issue tokens
     */
    @Transactional
    public TokenResponse login(LoginRequest request) {
        UserAuthResponse user = userClient.getUserForAuth(request.getEmail());
        if (user == null) {
            throw new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND);
        }

        // 1. Check if account is locked
        if (user.isLocked()) {
            throw new BusinessException(ErrorCode.ACCOUNT_LOCKED);
        }

        // 2. Check if account is inactive
        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new BusinessException(ErrorCode.ACCOUNT_INACTIVE);
        }

        // 3. Password match check
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            userClient.recordLoginFail(user.getEmail());
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        // 4. Record login success
        userClient.recordLoginSuccess(user.getEmail());

        return generateAndSaveToken(user);
    }

    /**
     * Reissue token (RTR)
     */
    @Transactional
    public TokenResponse reissue(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        Token storedToken = tokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new BusinessException(ErrorCode.TOKEN_NOT_FOUND));

        UserAuthResponse user = userClient.getUserForAuth(storedToken.getEmail());
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        tokenRepository.delete(storedToken);

        return generateAndSaveToken(user);
    }

    @Transactional
    public void logout(String refreshToken) {
        jwtTokenProvider.validateToken(refreshToken);
        String email = jwtTokenProvider.getUserEmailFromJWT(refreshToken);
        tokenRepository.deleteByEmail(email);
    }
}
