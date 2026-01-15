package com.mycompany.project.user.query.service;

import com.mycompany.project.user.command.domain.aggregate.Role;
import com.mycompany.project.user.command.domain.aggregate.User;
import com.mycompany.project.user.command.domain.repository.StudentDetailRepository;
import com.mycompany.project.user.command.domain.repository.TeacherDetailRepository;
import com.mycompany.project.user.command.domain.repository.UserRepository;
import com.mycompany.project.user.query.dto.UserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserQueryServiceTest {

    @InjectMocks
    private UserQueryService userQueryService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StudentDetailRepository studentDetailRepository;

    @Mock
    private TeacherDetailRepository teacherDetailRepository;

    @Spy
    private ModelMapper modelMapper;

    @Test
    @DisplayName("내 정보 조회 성공")
    void getMyInfo_Success() {
        // given
        String email = "test@test.com"; // userId 대신 email 사용
        User mockUser = User.builder()
                .email(email)
                .name("테스터")
                .role(Role.STUDENT)
                .build();

        given(userRepository.findByEmail(email)).willReturn(Optional.of(mockUser));

        // when
        UserResponse response = userQueryService.getMyInfo(email);

        // then
        assertNotNull(response);
        assertEquals(email, response.getEmail());
        assertEquals("테스터", response.getName());
    }

    @Test
    @DisplayName("내 정보 조회 실패 - 사용자 없음")
    void getMyInfo_NotFound() {
        // given
        String email = "unknown@test.com";
        given(userRepository.findByEmail(email)).willReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () -> userQueryService.getMyInfo(email));
    }

    @Test
    @DisplayName("내부 사용자(Internal User) 조회 성공")
    void getInternalUser_Success() {
        // given
        Long userId = 1L;
        User mockUser = User.builder()
                .userId(userId)
                .email("test@test.com")
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));

        // when
        UserResponse response = userQueryService.getInternalUser(userId);

        // then
        assertNotNull(response);
        assertEquals(userId, response.getUserId());
    }
}
