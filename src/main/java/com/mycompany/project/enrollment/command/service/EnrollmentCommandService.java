package com.mycompany.project.enrollment.command.service;

import com.mycompany.project.exception.BusinessException;
import com.mycompany.project.exception.ErrorCode;
import com.mycompany.project.course.entity.Course;
import com.mycompany.project.course.repository.CourseRepository;
import com.mycompany.project.enrollment.command.dto.BulkEnrollmentResult;
import com.mycompany.project.enrollment.command.dto.EnrollmentApplyRequest;
import com.mycompany.project.enrollment.entity.Enrollment;
import com.mycompany.project.enrollment.repository.CartMapper;
import com.mycompany.project.enrollment.repository.CartRepository;
import com.mycompany.project.enrollment.repository.EnrollmentRepository;
import com.mycompany.project.user.command.domain.aggregate.StudentDetail;
import com.mycompany.project.user.command.domain.repository.StudentDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional // 전체 메서드에 트랜잭션 적용
@RequiredArgsConstructor
public class EnrollmentCommandService {

  private final EnrollmentRepository enrollmentRepository;
  private final CourseRepository courseRepository;
  private final StudentDetailRepository studentDetailRepository;
  private final CartRepository cartRepository;
  private final CartMapper cartMapper;

  /**
   * 단건 수강 신청
   */
  public Long register(Long userId, EnrollmentApplyRequest request) {
    StudentDetail studentDetail = studentDetailRepository.findById(userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.STUDENT_NOT_FOUND));

    Course course = courseRepository.findByIdWithLock(request.getCourseId())
        .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));

    if (enrollmentRepository.existsByStudentDetailAndCourse(studentDetail, course)) {
      throw new BusinessException(ErrorCode.ALREADY_ENROLLED);
    }

    // 수강 인원 증가 (Dirty Checking)
    course.increaseCurrentCount();

    Enrollment enrollment = new Enrollment(studentDetail, course);
    enrollmentRepository.save(enrollment);

    return enrollment.getEnrollmentId();
  }

  /**
   * 👇 [추가] 수강 신청 취소
   */
  public void cancel(Long userId, Long enrollmentId) {
    // 1. 수강 신청 내역 조회
    Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
        .orElseThrow(() -> new BusinessException(ErrorCode.ENROLLMENT_NOT_FOUND));

    // 2. 권한 확인 (본인의 신청 내역인지)
    // (Enrollment -> StudentDetail -> User(userId) 접근 경로가 있다고 가정)
    Long studentUserId = enrollment.getStudentDetail().getUser().getUserId();

    if (!studentUserId.equals(userId)) {
      throw new BusinessException(ErrorCode.NOT_YOUR_ENROLLMENT);
    }

    // 3. 수강 인원 감소 (Course 엔티티 메서드 호출)
    enrollment.getCourse().decreaseCurrentCount();

    // 4. 수강 신청 내역 삭제 (Hard Delete)
    enrollmentRepository.delete(enrollment);
  }


  /**
   * 장바구니 기반 일괄 신청
   */
  public List<BulkEnrollmentResult> bulkRegisterFromCart(Long userId) {
    // 1. 장바구니 조회 (MyBatis)
    List<Long> courseIdsInCart = cartMapper.findCourseIdsByStudentId(userId);

    if (courseIdsInCart.isEmpty()) {
      throw new BusinessException(ErrorCode.CART_EMPTY);
    }

    // 2. 일괄 처리 로직 위임
    return this.processBulkEnrollment(userId, courseIdsInCart);
  }

  /**
   * 내부 처리 로직 (Loop & Try-Catch)
   */
  private List<BulkEnrollmentResult> processBulkEnrollment(Long userId, List<Long> courseIds) {
    List<BulkEnrollmentResult> results = new ArrayList<>();

    // 학생 정보 미리 조회 (반복문 밖)
    StudentDetail student = studentDetailRepository.findById(userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.STUDENT_NOT_FOUND));

    for (Long courseId : courseIds) {
      try {
        // 1. 수강 신청 시도
        // DTO 생성자가 없다면 Builder 사용: EnrollmentApplyRequest.builder().courseId(courseId).build()
        register(userId, new EnrollmentApplyRequest(courseId));

        // 2. 성공 시 장바구니 삭제
        // getReferenceById는 DB 조회 없이 프록시 객체만 가져옴 (성능 최적화)
        Course courseRef = courseRepository.getReferenceById(courseId);
        cartRepository.deleteByStudentDetailAndCourse(student, courseRef);

        results.add(new BulkEnrollmentResult(courseId, "성공", true, "신청 완료"));

      } catch (BusinessException e) {
        // 3. 비즈니스 로직 실패 (만석, 중복 등) -> 실패 사유 기록
        results.add(new BulkEnrollmentResult(courseId, "실패", false, e.getErrorCode().getMessage()));
      } catch (Exception e) {
        // 4. 기타 시스템 에러
        results.add(new BulkEnrollmentResult(courseId, "에러", false, "시스템 오류"));
      }
    }

    return results;
  }
}