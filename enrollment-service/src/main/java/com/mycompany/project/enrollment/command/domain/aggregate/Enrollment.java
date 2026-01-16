package com.mycompany.project.enrollment.command.domain.aggregate;

import com.mycompany.project.common.entity.BaseEntity;
import com.mycompany.project.common.enums.EnrollmentStatus;
import com.mycompany.project.exception.BusinessException;
import com.mycompany.project.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(name = "tbl_enrollment", uniqueConstraints = {
    @UniqueConstraint(name = "uk_enrollment_student_course", columnNames = { "student_detail_id", "course" })
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Enrollment extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "enrollment_id")
  private Long enrollmentId;

  @Column(name = "student_detail_id", nullable = false)
  private Long studentDetailId;

  @Column(name = "course", nullable = false)
  private Long courseId;

  @Enumerated(EnumType.STRING)
  private EnrollmentStatus status;

  @Column(name = "cancellation_reason")
  private String cancellationReason;

  @Column(name = "memo", length = 500)
  private String memo;

  @Builder
  public Enrollment(Long studentDetailId, Long courseId) {
    this.studentDetailId = studentDetailId;
    this.courseId = courseId;
    this.status = EnrollmentStatus.APPLIED;
  }

  public void cancel() {
    if (this.status == EnrollmentStatus.FORCED_CANCELED || this.status == EnrollmentStatus.CANCELED) {
      throw new BusinessException(ErrorCode.ALREADY_CANCELED);
    }
    this.status = EnrollmentStatus.CANCELED;
  }

  public void forceCancel(String reason) {
    if (this.status == EnrollmentStatus.FORCED_CANCELED) {
      throw new BusinessException(ErrorCode.ALREADY_CANCELED);
    }
    this.status = EnrollmentStatus.FORCED_CANCELED;
    this.cancellationReason = reason;
  }

  public void updateMemo(String memo) {
    this.memo = memo;
  }
}