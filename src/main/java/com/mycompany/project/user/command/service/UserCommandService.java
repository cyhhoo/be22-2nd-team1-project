package com.mycompany.project.user.command.service;

import com.mycompany.project.user.command.dto.AccountActivationRequest;
import com.mycompany.project.user.command.dto.UserRegisterRequest;
import com.mycompany.project.user.entity.User;
import com.mycompany.project.user.entity.UserStatus;
import com.mycompany.project.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserCommandService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserCommandService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional
  public Long registerUser(UserRegisterRequest request) {
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

  public void activateAccount(AccountActivationRequest request) {
    // 1. 사용자 조회
    User user = userRepository.findByEmail(request.getEmail())
        // .orElseThrow(IllegalArgumentException::new);
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사람 입니다."));

    // 2. 이미 활성화 된 경우. (아마 없을거 같긴 한데)
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
  }
}
