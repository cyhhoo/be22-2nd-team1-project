package com.mycompany.project.user.query.service;
import com.mycompany.project.user.command.domain.aggregate.Role;
import com.mycompany.project.user.command.domain.aggregate.User;
import com.mycompany.project.user.command.domain.repository.UserRepository;
import com.mycompany.project.user.query.dto.UserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserQueryServiceTest {
  @InjectMocks
  private UserQueryService userQueryService;
  @Mock
  private UserRepository userRepository;
  @Mock
  private ModelMapper modelMapper;

  @Test
  @DisplayName("관리자는 전체 회원 목록을 조회할 수 있다 (페이징)")
  void getUserList_success() {

    // given
    Pageable pageable = PageRequest.of(0, 10);
    User user1 = User.builder().email("user1@test.com").name("유저1").role(Role.STUDENT).build();
    User user2 = User.builder().email("user2@test.com").name("유저2").role(Role.TEACHER).build();
    Page<User> userPage = new PageImpl<>(List.of(user1, user2));
    given(userRepository.findAll(pageable)).willReturn(userPage);

    // Mocking: ModelMapper 매핑
    given(modelMapper.map(user1, UserResponse.class)).willReturn(new UserResponse());
    given(modelMapper.map(user2, UserResponse.class)).willReturn(new UserResponse());

    // when
    Page<UserResponse> result = userQueryService.getUserList(pageable);

    // then
    assertEquals(2, result.getTotalElements());
    verify(userRepository).findAll(pageable);
    verify(modelMapper, times(2)).map(any(User.class), eq(UserResponse.class));
  }

  @Test
  @DisplayName("이메일로 내 정보를 조회할 수 있다")
  void getMyInfo_success() {

    // given
    String email = "test@test.com";
    User user = User.builder().email(email).name("테스트").build();

    UserResponse response = new UserResponse();
    response.setEmail(email);
    response.setName("테스트");

    given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
    given(modelMapper.map(user, UserResponse.class)).willReturn(response);

    // when
    UserResponse result = userQueryService.getMyInfo(email);

    // then
    assertNotNull(result);
    assertEquals(email, result.getEmail());
    assertEquals("테스트", result.getName());
  }
}