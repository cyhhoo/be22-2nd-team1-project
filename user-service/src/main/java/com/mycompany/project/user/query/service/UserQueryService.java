package com.mycompany.project.user.query.service;

import com.mycompany.project.user.command.domain.aggregate.User;
import com.mycompany.project.user.command.domain.repository.StudentDetailRepository;
import com.mycompany.project.user.command.domain.repository.TeacherDetailRepository;
import com.mycompany.project.user.command.domain.repository.UserRepository;
import com.mycompany.project.user.query.dto.InternalStudentResponse;
import com.mycompany.project.user.query.dto.InternalTeacherResponse;
import com.mycompany.project.user.query.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryService {

  private final UserRepository userRepository;
  private final StudentDetailRepository studentDetailRepository;
  private final TeacherDetailRepository teacherDetailRepository;
  private final ModelMapper modelMapper;

  /**
   * 이메일로 내 정보 조회 하기
   * 
   * @param email 내 이메일(userId)
   * @return 내 정보(entity -> dto)
   */
  public UserResponse getMyInfo(String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

    return modelMapper.map(user, UserResponse.class);
  }

  public UserResponse getInternalUser(Long userId) {
    return userRepository.findById(userId)
        .map(user -> modelMapper.map(user, UserResponse.class))
        .orElse(null);
  }

  public Page<UserResponse> getUserList(Pageable pageable) {
    Page<User> users = userRepository.findAll(pageable);
    return users.map(user -> modelMapper.map(user, UserResponse.class));
  }

  public InternalTeacherResponse getTeacherInfo(Long userId) {
    return teacherDetailRepository.findById(userId)
        .map(detail -> modelMapper.map(detail, InternalTeacherResponse.class))
        .orElse(null);
  }

  public InternalStudentResponse getStudentInfo(Long userId) {
    return studentDetailRepository.findById(userId)
        .map(detail -> modelMapper.map(detail, InternalStudentResponse.class))
        .orElse(null);
  }

  public long countMatchedStudents(List<Long> studentIds, Integer grade, String classNo) {
    return studentDetailRepository.countByIdInAndGradeAndClassNo(studentIds, grade, classNo);
  }

  public List<InternalStudentResponse> searchStudents(
      com.mycompany.project.user.query.dto.StudentSearchRequest request) {
    if (request.getGrade() != null && request.getClassNo() != null) {
      // 학년/반으로 조회
      return studentDetailRepository.findByGradeAndClassNo(request.getGrade(), request.getClassNo())
          .stream()
          .map(detail -> modelMapper.map(detail, InternalStudentResponse.class))
          .toList();
    } else if (request.getName() != null) {
      // 이름으로 조회 (User join 필요)
      // 간단하게 전체에서 필터링하거나 Repository에 메소드 추가 필요. 여기서는 Repository 메소드 없으니 findAll 후 필터링
      // (임시)
      return studentDetailRepository.findAll().stream()
          .filter(s -> s.getUser().getName().contains(request.getName()))
          .map(detail -> modelMapper.map(detail, InternalStudentResponse.class))
          .toList();
    }
    return List.of();
  }
}
