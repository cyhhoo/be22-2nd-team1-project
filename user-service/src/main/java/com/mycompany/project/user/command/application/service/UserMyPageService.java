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
  public void changePassword(String email, ChangePasswordRequest request) {

    // 1. Find user
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("User not found or password mismatch."));

    // 2. Verify current password
    if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
      throw new IllegalArgumentException("Current password does not match.");
    }

    // 3. Check duplication with last 3 passwords
    List<PasswordHistory> passwordHistories = passwordHistoryRepository.findTop3ByUserOrderByCreatedAtDesc(user);

    for (PasswordHistory passwordHistory : passwordHistories) {
      if (passwordEncoder.matches(request.getNewPassword(), passwordHistory.getPassword())) {
        throw new IllegalArgumentException("Cannot use recently used passwords.");
      }
    }

    // 4. Backup current password to History
    PasswordHistory history = PasswordHistory.builder()
        .user(user)
        .password(user.getPassword())
        .build();
    passwordHistoryRepository.save(history);

    // 5. Change to new password
    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
  }
}
