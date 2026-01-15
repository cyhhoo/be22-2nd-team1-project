package com.mycompany.project.auth.command.service;

import com.mycompany.project.auth.command.dto.AccountActivationRequest;
import com.mycompany.project.auth.command.dto.UserRegisterRequest;
import com.mycompany.project.user.command.domain.aggregate.Role;
import com.mycompany.project.user.command.domain.aggregate.User;
import com.mycompany.project.user.command.domain.aggregate.UserStatus;
import com.mycompany.project.user.command.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthCommandServiceTest {

  @InjectMocks
  private AuthCommandService authCommandService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Test
  @DisplayName("회원가입 성공: 초기 상태 INACTIVE 및 비밀번호 암호화 확인")
  void registerUser_Success() {
    // Given
    UserRegisterRequest request = new UserRegisterRequest();
    request.setEmail("newuser@example.com");
    request.setPassword("password123");
    request.setName("New User");
    request.setRole(Role.STUDENT);
    request.setBirthDate("19990101");

    given(userRepository.existsByEmail(request.getEmail())).willReturn(false);
    given(passwordEncoder.encode(request.getPassword())).willReturn("encodedPassword");

    User savedUser = User.builder().userId(1L).email(request.getEmail()).build();
    given(userRepository.save(any(User.class))).willReturn(savedUser);

    // When
    Long userId = authCommandService.registerUser(request);

    // Then
    assertEquals(1L, userId);
    verify(userRepository, times(1)).save(any(User.class));
  }

  @Test
  @DisplayName("계정 활성화 성공: 상태 변경 ACTIVE")
  void activateAccount_Success() {
    // Given
    String email = "inactive@example.com";
    User inactiveUser = User.builder()
        .email(email)
        .name("Inactive")
        .password("oldPass")
        .role(Role.STUDENT)
        .status(UserStatus.INACTIVE)
        .birthDate("20000101")
        .authCode("000101") // 생년월일 뒷자리(가정)
        .build();

    AccountActivationRequest request = new AccountActivationRequest();
    request.setEmail(email);
    request.setName("Inactive");
    request.setBirthDate("20000101");
    request.setAuthCode("000101");
    request.setNewPassword("newPassword123");

    given(userRepository.findByEmail(email)).willReturn(Optional.of(inactiveUser));
    given(passwordEncoder.encode(request.getNewPassword())).willReturn("encodedNewPassword");

    // When
    authCommandService.activateAccount(request);

    // Then
    assertEquals(UserStatus.ACTIVE, inactiveUser.getStatus());
    assertEquals("encodedNewPassword", inactiveUser.getPassword());
    verify(userRepository, times(1)).save(inactiveUser);
  }

  @Test
  @DisplayName("계정 활성화 실패: 인증 정보 불일치")
  void activateAccount_Fail_WrongInfo() {
    // Given
    String email = "inactive@example.com";
    User inactiveUser = User.builder()
        .email(email)
        .name("Inactive")
        .birthDate("20000101")
        .authCode("000101")
        .status(UserStatus.INACTIVE)
        .build();

    AccountActivationRequest request = new AccountActivationRequest();
    request.setEmail(email);
    request.setName("DifferentName"); // 이름 불일치
    request.setBirthDate("20000101");

    given(userRepository.findByEmail(email)).willReturn(Optional.of(inactiveUser));

    // When & Then
    assertThrows(IllegalArgumentException.class, () -> authCommandService.activateAccount(request));
  }
}