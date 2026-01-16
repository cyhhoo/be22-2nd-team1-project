package com.mycompany.project.enrollment.command.application.service;

import com.mycompany.project.exception.BusinessException;
import com.mycompany.project.exception.ErrorCode;
import com.mycompany.project.enrollment.command.application.dto.BulkEnrollmentResult;
import com.mycompany.project.enrollment.command.application.dto.EnrollmentApplyRequest;
import com.mycompany.project.enrollment.command.domain.aggregate.Enrollment;
import com.mycompany.project.enrollment.command.domain.repository.CartMapper;
import com.mycompany.project.enrollment.command.domain.repository.CartRepository;
import com.mycompany.project.enrollment.command.domain.repository.EnrollmentRepository;
import com.mycompany.project.enrollment.client.CourseClient;
import com.mycompany.project.enrollment.client.InternalCourseResponse;
import com.mycompany.project.enrollment.client.UserClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EnrollmentCommandService {

  private final EnrollmentRepository enrollmentRepository;
  private final CourseClient courseClient;
  private final UserClient userClient;
  private final CartRepository cartRepository;
  private final CartMapper cartMapper;

  public Long register(Long studentDetailId, EnrollmentApplyRequest request) {
    // 1. Get student grade info (Feign call)
    Integer studentGrade = userClient.getStudentGrade(studentDetailId);
    if (studentGrade == null) {
      throw new BusinessException(ErrorCode.STUDENT_NOT_FOUND);
    }

    // 2. Get course info (Feign)
    InternalCourseResponse newCourse = courseClient.getInternalCourseInfo(request.getCourseId());
    if (newCourse == null) {
      throw new BusinessException(ErrorCode.COURSE_NOT_FOUND);
    }

    // 3. [Validation] Grade restriction check
    if (newCourse.getTargetGrade() != null && !newCourse.getTargetGrade().equals(studentGrade)) {
      throw new BusinessException(ErrorCode.COURSE_CONDITION_MISMATCH);
    }

    // 4. [Validation] Time conflict check
    validateTimeConflict(studentDetailId, newCourse);

    // 5. [Validation] Duplicate enrollment check
    if (enrollmentRepository.existsByStudentDetailIdAndCourseId(studentDetailId, request.getCourseId())) {
      throw new BusinessException(ErrorCode.ALREADY_ENROLLED);
    }

    // 6. Increase enrollment count API call (Feign)
    courseClient.increaseEnrollment(request.getCourseId());

    try {
      // 7. Save enrollment record
      Enrollment enrollment = new Enrollment(studentDetailId, request.getCourseId());
      return enrollmentRepository.save(enrollment).getEnrollmentId();

    } catch (Exception e) {
      // 8. [Compensation Transaction] Rollback count on save failure
      log.error("Enrollment save failed, executing compensation transaction: courseId={}", request.getCourseId());
      courseClient.decreaseEnrollment(request.getCourseId());
      throw e;
    }
  }

  private void validateTimeConflict(Long studentDetailId, InternalCourseResponse newCourse) {
    if (newCourse.getTimeSlots() == null || newCourse.getTimeSlots().isEmpty()) {
      return;
    }

    List<Long> enrolledCourseIds = enrollmentRepository.findEnrolledCourseIds(studentDetailId);

    for (Long enrolledCourseId : enrolledCourseIds) {
      InternalCourseResponse enrolledCourse = courseClient.getInternalCourseInfo(enrolledCourseId);
      if (enrolledCourse == null || enrolledCourse.getTimeSlots() == null)
        continue;

      for (InternalCourseResponse.TimeSlotResponse newSlot : newCourse.getTimeSlots()) {
        for (InternalCourseResponse.TimeSlotResponse enrolledSlot : enrolledCourse.getTimeSlots()) {
          if (newSlot.getDayOfWeek().equals(enrolledSlot.getDayOfWeek()) &&
              newSlot.getPeriod().equals(enrolledSlot.getPeriod())) {
            throw new BusinessException(ErrorCode.TIME_CONFLICT);
          }
        }
      }
    }
  }

  public void cancel(Long studentDetailId, Long enrollmentId) {
    Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
        .orElseThrow(() -> new BusinessException(ErrorCode.ENROLLMENT_NOT_FOUND));

    if (!enrollment.getStudentDetailId().equals(studentDetailId)) {
      throw new BusinessException(ErrorCode.NOT_YOUR_ENROLLMENT);
    }

    courseClient.decreaseEnrollment(enrollment.getCourseId());

    try {
      enrollment.cancel();
      enrollmentRepository.save(enrollment);
    } catch (Exception e) {
      log.error("Enrollment cancel failed, executing compensation transaction: enrollmentId={}", enrollmentId);
      courseClient.increaseEnrollment(enrollment.getCourseId());
      throw e;
    }
  }

  public List<BulkEnrollmentResult> bulkRegisterFromCart(Long studentDetailId) {
    List<Long> courseIdsInCart = cartMapper.findCourseIdsByStudentId(studentDetailId);

    if (courseIdsInCart.isEmpty()) {
      throw new BusinessException(ErrorCode.CART_EMPTY);
    }

    List<BulkEnrollmentResult> results = new ArrayList<>();
    for (Long courseId : courseIdsInCart) {
      try {
        register(studentDetailId, new EnrollmentApplyRequest(courseId));
        cartRepository.deleteByStudentDetailIdAndCourseId(studentDetailId, courseId);
        results.add(new BulkEnrollmentResult(courseId, "SUCCESS", true, "Enrollment completed"));
      } catch (BusinessException e) {
        results.add(new BulkEnrollmentResult(courseId, "FAILED", false, e.getErrorCode().getMessage()));
      } catch (Exception e) {
        results.add(new BulkEnrollmentResult(courseId, "ERROR", false, "System error"));
      }
    }
    return results;
  }
}
