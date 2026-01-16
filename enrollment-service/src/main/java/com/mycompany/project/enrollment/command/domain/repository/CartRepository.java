package com.mycompany.project.enrollment.command.domain.repository;

import com.mycompany.project.enrollment.command.domain.aggregate.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

  boolean existsByStudentDetailIdAndCourseId(Long studentDetailId, Long courseId);

  void deleteByStudentDetailIdAndCourseId(Long studentDetailId, Long courseId);

  void deleteByStudentDetailId(Long studentDetailId);
}