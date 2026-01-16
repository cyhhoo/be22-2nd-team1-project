package com.mycompany.project.enrollment.command.application.service;

import com.mycompany.project.enrollment.command.application.dto.CartAddRequest;
import com.mycompany.project.enrollment.command.domain.aggregate.Cart;
import com.mycompany.project.enrollment.command.domain.repository.CartRepository;
import com.mycompany.project.exception.BusinessException;
import com.mycompany.project.exception.ErrorCode;
import com.mycompany.project.enrollment.client.CourseClient;
import com.mycompany.project.enrollment.client.InternalCourseResponse;
import com.mycompany.project.enrollment.client.UserClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CartCommandService {

  private final CartRepository cartRepository;
  private final CourseClient courseClient;
  private final UserClient userClient;

  public Long addCart(Long studentDetailId, CartAddRequest request) {
    // 1. ?숈깮 議댁옱 ?щ? ?뺤씤 (Feign)
    if (!userClient.existsByStudentId(studentDetailId)) {
      throw new BusinessException(ErrorCode.STUDENT_NOT_FOUND);
    }

    // 2. 媛뺤쥖 議고쉶 (Feign)
    InternalCourseResponse course = courseClient.getInternalCourseInfo(request.getCourseId());
    if (course == null) {
      throw new BusinessException(ErrorCode.COURSE_NOT_FOUND);
    }

    // 3. 以묐났 泥댄겕
    if (cartRepository.existsByStudentDetailIdAndCourseId(studentDetailId, request.getCourseId())) {
      throw new BusinessException(ErrorCode.ALREADY_IN_CART);
    }

    // 4. ???
    Cart cart = new Cart(studentDetailId, request.getCourseId());
    cartRepository.save(cart);

    return cart.getCartId();
  }

  public void removeCart(Long studentDetailId, Long courseId) {
    if (!userClient.existsByStudentId(studentDetailId)) {
      throw new BusinessException(ErrorCode.STUDENT_NOT_FOUND);
    }

    cartRepository.deleteByStudentDetailIdAndCourseId(studentDetailId, courseId);
  }
}
