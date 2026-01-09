package com.mycompany.project.enrollment.repository;

import com.mycompany.project.course.entity.Course;
import com.mycompany.project.enrollment.entity.Enrollment;
import com.mycompany.project.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

  // [중복 신청 방지용] 이미 신청한 강좌인지 확인
  boolean existsByStudentAndCourse(User student, Course course);

  // (필요시) 특정 학생의 신청 취소를 위해 조회
  Optional<Enrollment> findByStudentAndCourse(User student, Course course);
}