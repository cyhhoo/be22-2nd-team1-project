package com.mycompany.project.enrollment.command.service;

import com.mycompany.project.exception.BusinessException;
import com.mycompany.project.exception.ErrorCode;
import com.mycompany.project.enrollment.command.dto.BulkEnrollmentResult;
import com.mycompany.project.enrollment.command.dto.EnrollmentApplyRequest;
import com.mycompany.project.enrollment.entity.Enrollment;
import com.mycompany.project.enrollment.repository.CartMapper;
import com.mycompany.project.enrollment.repository.CartRepository;
import com.mycompany.project.enrollment.repository.EnrollmentRepository;
import com.mycompany.project.user.command.domain.aggregate.StudentDetail;
import com.mycompany.project.user.command.domain.repository.StudentDetailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import com.mycompany.project.enrollment.client.CourseClient;
import com.mycompany.project.enrollment.client.InternalCourseResponse;

@Slf4j
@Service
@Transactional // 전체 메서드에 트랜잭션 적용
@RequiredArgsConstructor
public class EnrollmentCommandService {

  private final EnrollmentRepository enrollmentRepository;
  private final CourseClient courseClient;
  private final StudentDetailRepository studentDetailRepository;
  private final CartRepository cartRepository;
  private final CartMapper cartMapper;

  /**
   * 단건 수강 신청
   */
  public Long register(Long userId, EnrollmentApplyRequest request) {
    // 1. 학생 정보 조회
    StudentDetail studentDetail = studentDetailRepository.findById(userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.STUDENT_NOT_FOUND));

    // 2. 새 강좌 정보 조회 (Feign)
    InternalCourseResponse newCourse = courseClient.getInternalCourseInfo(request.getCourseId());
    if (newCourse == null) {
      throw new BusinessException(ErrorCode.COURSE_NOT_FOUND);
    }

    // 3. [검증] 학년 제한 체크
    if (newCourse.getTargetGrade() != null && !newCourse.getTargetGrade().equals(studentDetail.getGrade())) {
      throw new BusinessException(ErrorCode.COURSE_CONDITION_MISMATCH);
    }

    // 4. [검증] 시간표 중복 체크 (애플리케이션 레벨)
    validateTimeConflict(userId, newCourse);

    // 5. [검증] 동일 과목 중복 신청 체크
    if (enrollmentRepository.existsByStudentDetailAndCourseId(studentDetail, request.getCourseId())) {
      throw new BusinessException(ErrorCode.ALREADY_ENROLLED);
    }

    // 6. 수강 인원 증가 API 호출 (Feign)
    courseClient.increaseEnrollment(request.getCourseId());

    try {
      // 7. 수강 신청 내역 저장
      Enrollment enrollment = new Enrollment(studentDetail, request.getCourseId());
      return enrollmentRepository.save(enrollment).getEnrollmentId();

    } catch (Exception e) {
      // 8. [보상 트랜잭션] 저장 실패 시 인원수 다시 감소
      log.error("수강 신청 저장 실패로 인한 보상 트랜잭션 실행: courseId={}", request.getCourseId());
      courseClient.decreaseEnrollment(request.getCourseId());
      throw e;
    }
  }

  /**
   * 시간표 중복 검증 (애플리케이션 레벨)
   */
  private void validateTimeConflict(Long userId, InternalCourseResponse newCourse) {
    if (newCourse.getTimeSlots() == null || newCourse.getTimeSlots().isEmpty()) {
      return;
    }

    // 현재 학생이 수강 중인 과목 ID 리스트 조회
    List<Long> enrolledCourseIds = enrollmentRepository.findEnrolledCourseIds(userId);

    for (Long enrolledCourseId : enrolledCourseIds) {
      // 기존 수강 과목의 시간표 정보 조회 (Feign)
      InternalCourseResponse enrolledCourse = courseClient.getInternalCourseInfo(enrolledCourseId);
      if (enrolledCourse == null || enrolledCourse.getTimeSlots() == null) continue;

      // 새 과목의 시간표와 기존 과목의 시간표 비교
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

  /**
   * 수강 신청 취소
   */
  public void cancel(Long userId, Long enrollmentId) {
    // 1. 수강 신청 내역 조회
    Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
        .orElseThrow(() -> new BusinessException(ErrorCode.ENROLLMENT_NOT_FOUND));

    // 2. 권한 확인
    if (!enrollment.getStudentDetail().getUser().getUserId().equals(userId)) {
      throw new BusinessException(ErrorCode.NOT_YOUR_ENROLLMENT);
    }

    // 3. 수강 인원 감소 API 호출 (Feign)
    courseClient.decreaseEnrollment(enrollment.getCourseId());

    try {
      // 4. [핵심 변경] delete() 대신 엔티티의 cancel() 메서드 호출
      // 이렇게 하면 JPA의 Dirty Checking에 의해 status가 CANCELED로 업데이트됩니다.
      enrollment.cancel();
      enrollmentRepository.save(enrollment);

      // 5. [삭제] enrollmentRepository.delete(enrollment);

    } catch (Exception e) {
      // 보상 트랜잭션
      log.error("수강 취소 실패로 인한 보상 트랜잭션 실행: enrollmentId={}", enrollmentId);
      courseClient.increaseEnrollment(enrollment.getCourseId());
      throw e;
    }
  }

  /**
   * 장바구니 기반 일괄 신청
   */
  public List<BulkEnrollmentResult> bulkRegisterFromCart(Long studentId) {
    List<Long> courseIdsInCart = cartMapper.findCourseIdsByStudentId(studentId);

    if (courseIdsInCart.isEmpty()) {
      throw new BusinessException(ErrorCode.CART_EMPTY);
    }

    return this.processBulkEnrollment(studentId, courseIdsInCart);
  }

  /**
   * 내부 처리 로직 (Loop & Try-Catch)
   */
  private List<BulkEnrollmentResult> processBulkEnrollment(Long userId, List<Long> courseIds) {
    List<BulkEnrollmentResult> results = new ArrayList<>();

    StudentDetail student = studentDetailRepository.findById(userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.STUDENT_NOT_FOUND));

    for (Long courseId : courseIds) {
      try {
        register(userId, new EnrollmentApplyRequest(courseId));

        // 성공 시 장바구니 삭제
        cartRepository.deleteByStudentDetailAndCourseId(student, courseId);

        results.add(new BulkEnrollmentResult(courseId, "성공", true, "신청 완료"));

      } catch (BusinessException e) {
        results.add(new BulkEnrollmentResult(courseId, "실패", false, e.getErrorCode().getMessage()));
      } catch (Exception e) {
        results.add(new BulkEnrollmentResult(courseId, "에러", false, "시스템 오류"));
      }
    }

    return results;
  }
}
