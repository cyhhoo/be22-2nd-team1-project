package com.mycompany.project.enrollment.entity;

import com.mycompany.project.common.entity.BaseEntity;
import com.mycompany.project.course.entity.Course;
import com.mycompany.project.user.command.domain.aggregate.StudentDetail;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Getter
@Table(
    name = "tbl_enrollment",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_enrollment_student_course",
            columnNames = {"student_detail_id", "course_id"}
        )
    }
)
@SQLDelete(sql = "UPDATE tbl_enrollment SET status = 'CANCELLED' WHERE enrollment_id = ?")
@Where(clause = "status = 'APPLIED'")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Enrollment extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "enrollment_id")
  private Long enrollmentId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "student_detail_id", nullable = false)
  private StudentDetail studentDetail;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "course_id", nullable = false)
  private Course course;

  @Enumerated(EnumType.STRING)
  private EnrollmentStatus status;

  @Builder
  public Enrollment(StudentDetail studentDetail, Course course) {
    this.studentDetail = studentDetail;
    this.course = course;
    this.status = EnrollmentStatus.APPLIED;
  }

  // --- [핵심] 비즈니스 로직 (Command) ---
  // Setter 대신 메서드로 상태를 변경합니다.
  public void cancel() {
    if (this.status == EnrollmentStatus.FORCED_CANCELED) {
      throw new IllegalStateException("이미 강제 취소된 내역입니다.");
    }
    this.status = EnrollmentStatus.CANCELED;
  }
}

/*
 * Command는 JPA를 쓰고, **Query는 MyBatis(또는 QueryDSL)**를 쓴다.
 * Mapper가 query쪽, Command가 repository쪽
 */