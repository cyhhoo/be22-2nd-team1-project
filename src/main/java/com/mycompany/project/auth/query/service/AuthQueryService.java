package com.mycompany.project.auth.query.service;

import com.mycompany.project.auth.query.dto.LoginRequest;
import com.mycompany.project.auth.query.dto.TokenResponse;
import com.mycompany.project.common.exception.AccountInactiveException;
import com.mycompany.project.jwtsecurity.JwtTokenProvider;
import com.mycompany.project.user.entity.Token;
import com.mycompany.project.user.entity.User;
import com.mycompany.project.user.entity.UserStatus;
import com.mycompany.project.user.repository.TokenRepository;
import com.mycompany.project.user.repository.UserRepository;
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
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        // 1. 잠금 상태 확인 (제일 먼저)
        if (user.isLocked()) {
            throw new IllegalArgumentException("계정이 잠금되었습니다. 관리자에게 문의하세요.");
        }

        // 2. 비활성 상태 확인
        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new AccountInactiveException("계정이 활성화되지 않았습니다. 최초 비밀번호 변경 및 계정 활성화를 진행해주세요.");
        }

        // 3. 비밀번호 불일치 체크
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            // 실패 처리 호출
            user.loginFail();
            // 실패 남은 횟수 알려주기
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다. (실패 횟수: " + user.getLoginFailCount() + "/5)");
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
            throw new IllegalArgumentException("유효하지 않은 Refresh Token입니다.");
        }

        // 2. DB에 존재하는지 확인
        Token storedToken = tokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않거나 만료된 토큰입니다."));

        // 3. 사용자 정보 조회
        User user = userRepository.findByEmail(storedToken.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 4. 기존 토큰 삭제 (RTR - 일회용 사용)
        tokenRepository.delete(storedToken);

        // 5. 새 토큰 발급 및 저장
        return generateAndSaveToken(user);
    }
}
