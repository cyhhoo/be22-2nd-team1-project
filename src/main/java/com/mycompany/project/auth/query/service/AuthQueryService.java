package com.mycompany.project.auth.query.service;

import com.mycompany.project.auth.query.dto.LoginRequest;
import com.mycompany.project.auth.query.dto.TokenResponse;
import com.mycompany.project.exception.BusinessException;
import com.mycompany.project.exception.ErrorCode;
import com.mycompany.project.jwtsecurity.JwtTokenProvider;
import com.mycompany.project.user.command.domain.aggregate.Token;
import com.mycompany.project.user.command.domain.aggregate.User;
import com.mycompany.project.user.command.domain.aggregate.UserStatus;
import com.mycompany.project.user.command.domain.repository.TokenRepository;
import com.mycompany.project.user.command.domain.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthQueryService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthQueryService(UserRepository userRepository, TokenRepository tokenRepository,
            PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // 토큰 생성 및 저장
    private TokenResponse generateAndSaveToken(User user) {
        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

        Token tokenEntity = Token.builder()
                .token(refreshToken)
                .email(user.getEmail())
                .build();
        tokenRepository.save(tokenEntity);

        return new TokenResponse(accessToken, refreshToken);
    }

    /**
     * 로그인 및 토큰 발급
     */
    @Transactional
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));

        // 1. 잠금 상태 확인 (제일 먼저)
        if (user.isLocked()) {
            throw new BusinessException(ErrorCode.ACCOUNT_LOCKED);
        }

        // 2. 비활성 상태 확인
        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new BusinessException(ErrorCode.ACCOUNT_INACTIVE);
        }

        // 3. 비밀번호 불일치 체크
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            // 실패 처리 호출
            user.loginFail();
            // 실패 남은 횟수 알려주기
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }
        // 3. 로그인 성공 처리
        user.loginSuccess();

        // 토큰 생성 및 저장
        return generateAndSaveToken(user);
    }

    // 토큰 재발급 (RTR)
    @Transactional
    public TokenResponse reissue(String refreshToken) {
        // 1. 토큰 유효성 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        // 2. DB에 존재하는지 확인
        Token storedToken = tokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new BusinessException(ErrorCode.TOKEN_NOT_FOUND));

        // 3. 사용자 정보 조회
        User user = userRepository.findByEmail(storedToken.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 4. 기존 토큰 삭제 (RTR - 일회용 사용)
        tokenRepository.delete(storedToken);

        // 5. 새 토큰 발급 및 저장
        return generateAndSaveToken(user);
    }

    @Transactional
    public void logout(String refreshToken){

      jwtTokenProvider.validateToken(refreshToken);

      String email = jwtTokenProvider.getUserEmailFromJWT(refreshToken);

      tokenRepository.deleteByEmail(email);
    }
}
