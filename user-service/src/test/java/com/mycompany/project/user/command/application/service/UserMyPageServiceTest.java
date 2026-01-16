package com.mycompany.project.user.command.application.service;

import com.mycompany.project.user.command.application.dto.ChangePasswordRequest;
import com.mycompany.project.user.command.domain.aggregate.PasswordHistory;
import com.mycompany.project.user.command.domain.aggregate.User;
import com.mycompany.project.user.command.domain.repository.PasswordHistoryRepository;
import com.mycompany.project.user.command.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserMyPageServiceTest {

    @InjectMocks
    private UserMyPageService userMyPageService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordHistoryRepository passwordHistoryRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("鍮꾨?踰덊샇 蹂寃??깃났 - ?대젰 ????뺤씤")
    void changePassword_Success() {
        // given
        String email = "test@test.com";
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("oldPass");
        request.setNewPassword("newPass");

        User user = User.builder()
                .email(email)
                .password("encodedOldPass")
                .build();

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(passwordEncoder.matches("oldPass", "encodedOldPass")).willReturn(true);
        given(passwordHistoryRepository.findTop3ByUserOrderByCreatedAtDesc(user)).willReturn(Collections.emptyList());
        given(passwordEncoder.encode("newPass")).willReturn("encodedNewPass");

        // when
        userMyPageService.changePassword(email, request);

        // then
        verify(passwordHistoryRepository, times(1)).save(any(PasswordHistory.class));
        verify(passwordEncoder).encode("newPass");
    }

    @Test
    @DisplayName("鍮꾨?踰덊샇 蹂寃??ㅽ뙣 - 理쒓렐 ?ъ슜??鍮꾨?踰덊샇")
    void changePassword_Fail_RecentPassword() {
        // given
        String email = "test@test.com";
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("oldPass");
        request.setNewPassword("recentPass");

        User user = User.builder()
                .email(email)
                .password("encodedOldPass")
                .build();

        PasswordHistory history = PasswordHistory.builder()
                .user(user)
                .password("encodedRecentPass")
                .build();

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(passwordEncoder.matches("oldPass", "encodedOldPass")).willReturn(true);
        given(passwordHistoryRepository.findTop3ByUserOrderByCreatedAtDesc(user)).willReturn(List.of(history));
        given(passwordEncoder.matches("recentPass", "encodedRecentPass")).willReturn(true);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> userMyPageService.changePassword(email, request));
    }
}
