package com.mycompany.project.enrollment.entity;

import com.mycompany.project.common.entity.BaseEntity; // 1. 생성일시 기록용
import com.mycompany.project.course.entity.Course;
import com.mycompany.project.user.command.domain.aggregate.StudentDetail;
import com.mycompany.project.user.command.domain.aggregate.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
// 2. [중복 방지] 같은 학생이 같은 과목을 장바구니에 두 번 담을 수 없도록 DB 제약조건 설정
@Table(
    name = "tbl_cart",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_cart_student_course",
            columnNames = {"student_detail_id", "course_id"}
        )
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 3. [안전성] 무분별한 객체 생성 방지
@AllArgsConstructor
@Builder
public class Cart extends BaseEntity { // BaseEntity 상속 (담은 날짜 created_at 자동 관리)

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "cart_id")
  private Long cartId;

  // Command 쪽은 객체 그래프 탐색과 무결성을 위해 연관관계 매핑 유지
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "student_detail_id", nullable = false)// 4. [무결성] 필수값(Not Null) 지정
  private StudentDetail studentDetail;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "course_id", nullable = false) // 4. [무결성] 필수값(Not Null) 지정
  private Course course;

  // --- 생성자 (Builder 패턴) ---
  @Builder
  public Cart(StudentDetail studentDetail, Course course) {
    this.studentDetail = studentDetail;
    this.course = course;
  }
}