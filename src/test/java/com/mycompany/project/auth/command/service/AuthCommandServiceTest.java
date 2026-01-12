package com.mycompany.project.auth.command.service;
import com.mycompany.project.auth.command.dto.AccountActivationRequest;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
class AuthCommandServiceTest {
  @InjectMocks
  private AuthCommandService authCommandService;
  @Mock
  private UserRepository userRepository;
  @Mock
  private PasswordEncoder passwordEncoder;

  @Test
  @DisplayName("계정 활성화 성공: 정상적인 정보 입력 시 ACTIVE 상태로 변경된다")
  void activateAccount_success() {

    // given
    String email = "test@test.com";
    String name = "테스트";
    String birthDate = "2000-01-01"; // YYMMDD -> 000101
    String authCode = "000101"; // AuthCommandService 로직 참고 (birthDate.substring(2))

    AccountActivationRequest request = new AccountActivationRequest();

    request.setEmail(email);
    request.setName(name);
    request.setBirthDate(birthDate);
    request.setAuthCode(authCode);
    request.setNewPassword("newPass123");

    User user = User.builder()
        .email(email)
        .name(name)
        .birthDate(birthDate)
        .authCode(authCode)
        .status(UserStatus.INACTIVE)
        .password("encodedOldPass")
        .build();

    given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
    given(passwordEncoder.encode("newPass123")).willReturn("encodedNewPass");

    // when
    authCommandService.activateAccount(request);

    // then
    assertEquals(UserStatus.ACTIVE, user.getStatus());
    assertEquals("encodedNewPass", user.getPassword());
  }

  @Test
  @DisplayName("계정 활성화 실패: 인증 코드가 일치하지 않으면 예외가 발생한다")
  void activateAccount_fail_invalidAuthCode() {

    // given
    String email = "test@test.com";

    AccountActivationRequest request = new AccountActivationRequest();
    request.setEmail(email);
    request.setName("테스트");
    request.setBirthDate("2000-01-01");
    request.setAuthCode("WRONG_CODE");

    User user = User.builder()
        .email(email)
        .name("테스트")
        .birthDate("2000-01-01")
        .authCode("000101")
        .status(UserStatus.INACTIVE)
        .build();

    given(userRepository.findByEmail(email)).willReturn(Optional.of(user));

    // when & then
    assertThrows(IllegalArgumentException.class, () -> {
      authCommandService.activateAccount(request);
    });
  }
}