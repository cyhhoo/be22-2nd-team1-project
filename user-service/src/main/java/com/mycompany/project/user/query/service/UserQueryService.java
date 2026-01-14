package com.mycompany.project.user.query.service;


import com.mycompany.project.user.command.domain.aggregate.User;
import com.mycompany.project.user.command.domain.repository.UserRepository;
import com.mycompany.project.user.query.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryService {

  private final UserRepository userRepository;
  private final ModelMapper modelMapper;

  /**
   * 이메일로 내 정보 조회 하기
   * @param email 내 이메일(userId)
   * @return 내 정보(entity -> dto)
   */
  public UserResponse getMyInfo(String email){
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

    return modelMapper.map(user,UserResponse.class);
  }

  /**
   * 관리자용 전체 회원 조회
   * @param pageable 페이징 정보
   * @return 전체 회원 리스트 Entity -> DTO 변환
   */
  public Page<UserResponse> getUserList(Pageable pageable){
    Page<User> users = userRepository.findAll(pageable);
    return users.map(user -> modelMapper.map(user, UserResponse.class));
  }
}
