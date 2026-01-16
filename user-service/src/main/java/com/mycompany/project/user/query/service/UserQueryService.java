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
   * Retrieve user info by email
   * 
   * @param email User email (unique)
   * @return UserResponse DTO
   */
  public UserResponse getMyInfo(String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));

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
      // Search by grade/class
      return studentDetailRepository.findByGradeAndClassNo(request.getGrade(), request.getClassNo())
          .stream()
          .map(detail -> modelMapper.map(detail, InternalStudentResponse.class))
          .toList();
    } else if (request.getName() != null) {
      // Search by name (filtering in memory for now, consider repository method for
      // performance)
      return studentDetailRepository.findAll().stream()
          .filter(s -> s.getUser().getName().contains(request.getName()))
          .map(detail -> modelMapper.map(detail, InternalStudentResponse.class))
          .toList();
    }
    return List.of();
  }
}
