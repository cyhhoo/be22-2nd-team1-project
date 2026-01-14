package com.mycompany.project.enrollment.command.service;

import com.mycompany.project.course.entity.Course;
import com.mycompany.project.course.repository.CourseRepository;
import com.mycompany.project.enrollment.command.dto.CartAddRequest;
import com.mycompany.project.enrollment.entity.Cart;
import com.mycompany.project.enrollment.repository.CartRepository;
import com.mycompany.project.exception.BusinessException;
import com.mycompany.project.exception.ErrorCode;
import com.mycompany.project.user.command.domain.repository.StudentDetailRepository;
import com.mycompany.project.user.command.domain.aggregate.StudentDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CartCommandService {

  private final CartRepository cartRepository;
  private final CourseRepository courseRepository;
  private final StudentDetailRepository studentDetailRepository; // UserRepo 대신 사용

  public Long addCart(Long userId, CartAddRequest request) {
    // 1. 학생 조회
    // (@MapsId를 사용하므로 userId가 곧 studentId입니다. findById 사용이 더 정확합니다.)
    StudentDetail student = studentDetailRepository.findById(userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.STUDENT_NOT_FOUND));

    // 2. 강좌 조회
    Course course = courseRepository.findById(request.getCourseId())
        .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));

    // 3. 중복 체크
    if (cartRepository.existsByStudentDetailAndCourse(student, course)) {
      throw new BusinessException(ErrorCode.ALREADY_IN_CART);
    }

    // 4. 저장
    Cart cart = new Cart(student, course);
    cartRepository.save(cart);

    return cart.getCartId();
  }

  public void removeCart(Long userId, Long courseId) {
    StudentDetail student = studentDetailRepository.findByUserId(userId)
        .orElseThrow(() -> new IllegalArgumentException("학생 정보를 찾을 수 없습니다."));

    Course course = courseRepository.findById(courseId)
        .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));

    cartRepository.deleteByStudentDetailAndCourse(student, course);
  }
}
