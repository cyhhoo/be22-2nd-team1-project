package com.mycompany.project.enrollment.entity;

import com.mycompany.project.common.entity.BaseEntity;
import com.mycompany.project.exception.BusinessException;
import com.mycompany.project.exception.ErrorCode;
import com.mycompany.project.user.command.domain.aggregate.StudentDetail;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Getter
@Table(name = "tbl_enrollment", uniqueConstraints = {
    @UniqueConstraint(name = "uk_enrollment_student_course", columnNames = { "student_detail_id", "course" })
})
// @SQLDelete(sql = "UPDATE tbl_enrollment SET status = 'CANCELED' WHERE enrollment_id = ?")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Enrollment extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "enrollment_id")
  private Long enrollmentId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "student_detail_id", nullable = false)
  private StudentDetail studentDetail;

  @Column(name = "course", nullable = false)
  private Long courseId;

  @Enumerated(EnumType.STRING)
  private EnrollmentStatus status;

  @Builder
  public Enrollment(StudentDetail studentDetail, Long courseId) {
    this.studentDetail = studentDetail;
    this.courseId = courseId;
    this.status = EnrollmentStatus.APPLIED;
  }

  @Column(name = "cancellation_reason") // enrollment entity에 cancellation_reason 컬럼추가 (취소사유)
  private String cancellationReason;

  @Column(name = "memo", length = 500) // enrollment entity에 memo 컬럼추가 (메모)
  private String memo;

  // 호환성 Getter (CourseService 등에서 getStudentDetail() 사용 시 지원)
  public StudentDetail getStudentDetail() {
    return this.studentDetail;
  }

  // Setter 대신 메서드로 상태를 변경합니다.
  public void cancel() {
    // [수정] 이미 취소된 상태(일반/강제 모두)라면 예외 발생
    if (this.status == EnrollmentStatus.FORCED_CANCELED || this.status == EnrollmentStatus.CANCELED) {
      throw new BusinessException(ErrorCode.ALREADY_CANCELED);
    }
    this.status = EnrollmentStatus.CANCELED;
  }

  public void forceCancel(String reason) {
    if (this.status == EnrollmentStatus.FORCED_CANCELED) {
      // 이미 강제 취소된 경우, 로직에 따라 예외를 던지거나 무시할 수 있음.
      // 여기서는 상태 변경 없이 리턴하거나 예외 처리. 계획서에 따라 구현.
      throw new BusinessException(ErrorCode.ALREADY_CANCELED);
      // Plan said: "이미 강제 취소된 경우... 예외 처리".
    }
    this.status = EnrollmentStatus.FORCED_CANCELED;
    this.cancellationReason = reason;
  }

  public void updateMemo(String memo) {
    this.memo = memo;
  }
}

/*
 * Command는 JPA를 쓰고, **Query는 MyBatis(또는 QueryDSL)**를 쓴다.
 * Mapper가 query쪽, Command가 repository쪽
 */