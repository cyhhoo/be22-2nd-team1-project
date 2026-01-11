package com.mycompany.project.enrollment.entity;

import com.mycompany.project.common.entity.BaseEntity;
import com.mycompany.project.course.entity.Course;
import com.mycompany.project.user.command.domain.aggregate.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "tbl_enrollment",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_enrollment_student_course",
            columnNames = {"student_id", "course_id"}
        )
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Enrollment extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long enrollmentId;

  // Command 쪽은 객체 그래프 탐색과 무결성을 위해 연관관계 매핑 유지
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "student_id", nullable = false)
  private User student;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "course_id", nullable = false)
  private Course course;

  @Enumerated(EnumType.STRING)
  private EnrollmentStatus status;

  @Builder
  public Enrollment(User student, Course course) {
    this.student = student;
    this.course = course;
    this.status = EnrollmentStatus.APPLIED;
  }

  public Long getStudentId() {
    return student != null ? student.getUserId() : null;
  }

  public Long getCourseId() {
    return course != null ? course.getId() : null;
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
 * */
