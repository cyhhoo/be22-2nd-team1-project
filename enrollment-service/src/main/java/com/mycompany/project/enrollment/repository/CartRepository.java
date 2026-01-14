package com.mycompany.project.enrollment.repository;

import com.mycompany.project.enrollment.entity.Cart;
import com.mycompany.project.user.command.domain.aggregate.StudentDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

  // [수정] User -> StudentDetail
  boolean existsByStudentDetailAndCourseId(StudentDetail studentDetail, Long courseId);

  // [수정] 삭제 시에도 StudentDetail 기준
  void deleteByStudentDetailAndCourseId(StudentDetail studentDetail, Long courseId);
}