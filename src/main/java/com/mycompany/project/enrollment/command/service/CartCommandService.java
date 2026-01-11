package com.mycompany.project.enrollment.command.service;

import com.mycompany.project.course.entity.Course;
import com.mycompany.project.course.repository.CourseRepository;
import com.mycompany.project.enrollment.command.dto.CartAddRequest;
import com.mycompany.project.enrollment.entity.Cart;
import com.mycompany.project.enrollment.repository.CartRepository;
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
    // 1. [핵심] userId -> StudentDetail 변환
    StudentDetail student = studentDetailRepository.findByUserId(userId)
        .orElseThrow(() -> new IllegalArgumentException("학생 정보를 찾을 수 없습니다. (학생 권한 아님)"));

    Course course = courseRepository.findById(request.getCourseId())
        .orElseThrow(() -> new IllegalArgumentException("강좌가 없습니다."));

    // 2. StudentDetail 객체로 중복 체크
    if (cartRepository.existsByStudentDetailAndCourse(student, course)) {
      throw new IllegalStateException("이미 장바구니에 있습니다.");
    }

    // 3. 저장
    Cart cart = new Cart(student, course);
    cartRepository.save(cart);

    return cart.getCartId();
  }

  public void removeCart(Long userId, Long courseId) {
    StudentDetail student = studentDetailRepository.findByUserId(userId)
        .orElseThrow(() -> new IllegalArgumentException("학생 정보를 찾을 수 없습니다."));

    Course course = courseRepository.findById(courseId)
        .orElseThrow(() -> new IllegalArgumentException("강좌가 없습니다."));

    cartRepository.deleteByStudentDetailAndCourse(student, course);
  }
}
