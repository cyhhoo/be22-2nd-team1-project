package com.mycompany.project.enrollment.repository;

import com.mycompany.project.course.entity.Course;
import com.mycompany.project.enrollment.entity.Enrollment;
import com.mycompany.project.enrollment.entity.EnrollmentStatus;
import com.mycompany.project.user.command.domain.aggregate.StudentDetail;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

  // 1. [중복 신청 방지] User가 아니라 StudentDetail 객체로 검사
  boolean existsByStudentDetailAndCourse(StudentDetail studentDetail, Course course);

  // [수정 전] 에러 발생 (Course.id를 찾으려 함)
  // List<Enrollment> findByCourseIdInAndStatus(List<Long> courseIds, EnrollmentStatus status);

  // [수정 후] JPQL로 "e.course.courseId"라고 명확히 지정
  @Query("SELECT e FROM Enrollment e WHERE e.course.courseId IN :courseIds AND e.status = :status")
  List<Enrollment> findByCourseIdInAndStatus(@Param("courseIds") List<Long> courseIds, @Param("status") EnrollmentStatus status);

  // 3. [시간표 중복 방지] 쿼리 경로 수정 (e.student -> e.studentDetail.user.userId)
  // Enrollment -> StudentDetail -> User -> userId 순으로 접근
  @Query("""
        SELECT count(e) > 0 
        FROM Enrollment e
        JOIN e.course c
        JOIN c.timeSlots ts
        WHERE e.studentDetail.user.userId = :userId
          AND e.status = 'APPLIED'
          AND ts.dayOfWeek = :dayOfWeek
          AND ts.period = :period
    """)
  boolean existsTimeConflict(
      @Param("userId") Long userId,
      @Param("dayOfWeek") String dayOfWeek,
      @Param("period") Integer period
  );
}