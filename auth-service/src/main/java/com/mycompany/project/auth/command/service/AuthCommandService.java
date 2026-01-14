package com.mycompany.project.auth.command.service;

import com.mycompany.project.auth.command.dto.AccountActivationRequest;
import com.mycompany.project.auth.query.dto.TokenResponse;
import com.mycompany.project.exception.BusinessException;
import com.mycompany.project.exception.ErrorCode;
import com.mycompany.project.security.JwtTokenProvider;
import com.mycompany.project.user.command.domain.aggregate.Token;
import com.mycompany.project.user.command.domain.aggregate.User;
import com.mycompany.project.user.command.domain.aggregate.UserStatus;
import com.mycompany.project.user.command.domain.repository.TokenRepository;
import com.mycompany.project.user.command.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthCommandService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenRepository tokenRepository;

    @Transactional
    public TokenResponse activateAccount(AccountActivationRequest request) {
        // 1. 사용자 조회
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 2. 이미 활성화 된 경우
        if (user.getStatus() == UserStatus.ACTIVE) {
            throw new IllegalArgumentException("이미 활성화 된 계정입니다.");
        }

        // 3. 본인 인증
        if (!(user.getName().equals(request.getName()) && user.getBirthDate().equals(request.getBirthDate()))) {
            throw new IllegalArgumentException("사용자 정보가 일치하지 않습니다.");
        }

        // 인증 코드 체크
        if (user.getAuthCode() != null && !user.getAuthCode().equals(request.getAuthCode())) {
            throw new IllegalArgumentException("인증 코드가 일치하지 않습니다.");
        }

        // 4. 활성화
        user.activate(passwordEncoder.encode(request.getNewPassword()));

        // 5. 저장
        userRepository.save(user);

        // 활성화 된 상태로 바로 즉시 토큰 발급
        String accessToken = jwtTokenProvider.createAccessToken(
                user.getUserId(), user.getEmail(), user.getRole(), user.getStatus());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

        // DB에 리프레시 토큰 저장
        Token tokenEntity = Token.builder()
                .token(refreshToken)
                .email(user.getEmail())
                .build();
        tokenRepository.save(tokenEntity);

        return new TokenResponse(accessToken, refreshToken);
    }
}
