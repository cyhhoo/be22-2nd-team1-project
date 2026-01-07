package com.mycompany.project.enrollment.repository;

import com.mycompany.project.course.entity.Course;
import com.mycompany.project.enrollment.entity.Cart;
import com.mycompany.project.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

  // [중복 담기 방지용] 이미 장바구니에 있는지 확인
  boolean existsByStudentAndCourse(User student, Course course);

  // 장바구니에서 특정 과목 삭제할 때 사용
  void deleteByStudentAndCourse(User student, Course course);
}