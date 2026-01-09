package com.mycompany.project.user.command.application.service;

import com.mycompany.project.user.command.application.dto.ChangePasswordRequest;
import com.mycompany.project.user.command.domain.aggregate.PasswordHistory;
import com.mycompany.project.user.command.domain.aggregate.User;
import com.mycompany.project.user.command.domain.repository.PasswordHistoryRepository;
import com.mycompany.project.user.command.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserMyPageService {

  private final UserRepository userRepository;
  private final PasswordHistoryRepository passwordHistoryRepository;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public void changePassword(String email, ChangePasswordRequest request){

    // 1. 유저 조회
    User user = userRepository.findByEmail(email)
        .orElseThrow(()->new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다."));

    // 2. 현재 비밀번호 확인
    if (!passwordEncoder.matches(request.getCurrentPassword(),user.getPassword())){
      throw new IllegalArgumentException("비밀 번호가 일치하지 않습니다");
    }

    // 3. 최근 비밀번호 3개와 중복 체크
    List<PasswordHistory> passwordHistories = passwordHistoryRepository.findTop3ByUserOrderByCreatedAtDesc(user);

    for (PasswordHistory passwordHistory : passwordHistories){
      if (passwordEncoder.matches(request.getNewPassword(),passwordHistory.getPassword())){
        throw new IllegalArgumentException("최근 사용한 비밀번호는 사용할 수 없습니다.");
      }
    }

    // 4. 기존 비밀번호를 History에 백업
    PasswordHistory history = PasswordHistory.builder()
        .user(user)
        .password(user.getPassword())
        .build();
    passwordHistoryRepository.save(history);

    // 5. 새 비밀번호로 변경
    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
  }
}
