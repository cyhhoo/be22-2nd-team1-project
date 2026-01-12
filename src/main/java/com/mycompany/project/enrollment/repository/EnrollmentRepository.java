package com.mycompany.project.enrollment.repository;

import com.mycompany.project.course.entity.Course;
import com.mycompany.project.enrollment.entity.Enrollment;
import com.mycompany.project.enrollment.entity.EnrollmentStatus;
import com.mycompany.project.user.command.domain.aggregate.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

  // [중복 신청 방지용] 이미 신청한 강좌인지 확인
  boolean existsByStudentAndCourse(User student, Course course);

  // (필요시) 특정 학생의 신청 취소를 위해 조회
  Optional<Enrollment> findByStudentAndCourse(User student, Course course);

  // 강좌별 수강신청 목록 조회
  List<Enrollment> findByCourseId(Long courseId);

  @Query("select e from Enrollment e where e.course.id = :courseId and e.status = :status")
  List<Enrollment> findByCourseIdAndStatus(@Param("courseId") Long courseId,
                                           @Param("status") EnrollmentStatus status);

  @Query("select e from Enrollment e where e.course.id in :courseIds and e.status = :status")
  List<Enrollment> findByCourseIdInAndStatus(@Param("courseIds") List<Long> courseIds,
                                             @Param("status") EnrollmentStatus status);
}
