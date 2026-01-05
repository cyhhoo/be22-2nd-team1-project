package com.mycompany.project.user.query.service;

import com.mycompany.project.user.query.dto.LoginRequest;
import com.mycompany.project.user.entity.User;
import com.mycompany.project.user.query.dto.TokenResponse;
import com.mycompany.project.user.repository.UserRepository;
import com.mycompany.project.common.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserQueryService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;

  public UserQueryService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtTokenProvider = jwtTokenProvider;
  }

  @Transactional(readOnly = true)
  public TokenResponse login(LoginRequest request) {

    User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
    }

    String accessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getRole());
    String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

    return new TokenResponse(accessToken, refreshToken);
  }
}
