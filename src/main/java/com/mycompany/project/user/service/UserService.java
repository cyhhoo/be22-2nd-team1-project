package com.mycompany.project.user.service;

import com.mycompany.project.user.dto.SignupRequest;
import com.mycompany.project.user.entity.User;
import com.mycompany.project.user.entity.UserStatus;
import com.mycompany.project.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final com.mycompany.project.common.security.JwtTokenProvider jwtTokenProvider;

  @Transactional
  public Long signup(SignupRequest request) {
    // 1. 이메일 중복 검사
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
    }

    // 2. 초기 상태 설정 (일단 INACTIVE로 시작)
    UserStatus initialStatus = UserStatus.INACTIVE;

    // 3. User 엔티티 생성 (Builder 패턴 사용)
    User user = User.builder()
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword())) // 비밀번호 암호화 필수!
        .name(request.getName())
        .role(request.getRole())
        .status(initialStatus)
        .birthDate(request.getBirthDate())
        .authCode(request.getBirthDate().substring(2)) // 생년월일 6자리로 임시 인증코드 생성
        .build();

    // 4. 저장
    return userRepository.save(user).getUserId();
  }

  @Transactional(readOnly = true)
  public com.mycompany.project.user.dto.TokenResponse login(com.mycompany.project.user.dto.LoginRequest request) {
    // 1. 이메일 검증
    com.mycompany.project.user.entity.User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));
    // 2. 비밀번호 검증
    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
    }
    // 3. 토큰 발급
    String accessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getRole());
    String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());
    return new com.mycompany.project.user.dto.TokenResponse(accessToken, refreshToken);
  }

}