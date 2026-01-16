package com.mycompany.project.enrollment.command.domain.aggregate;

import com.mycompany.project.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_cart", uniqueConstraints = {
    @UniqueConstraint(name = "uk_cart_student_course", columnNames = { "student_detail_id", "course" })
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Cart extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "cart_id")
  private Long cartId;

  @Column(name = "student_detail_id", nullable = false)
  private Long studentDetailId;

  @Column(name = "course", nullable = false)
  private Long courseId;

  @Builder
  public Cart(Long studentDetailId, Long courseId) {
    this.studentDetailId = studentDetailId;
    this.courseId = courseId;
  }
}