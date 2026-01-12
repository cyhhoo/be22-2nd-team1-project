package com.mycompany.project.enrollment.repository;

import com.mycompany.project.course.entity.Course;
import com.mycompany.project.enrollment.entity.Enrollment;
import com.mycompany.project.user.command.domain.aggregate.User;
import com.mycompany.project.enrollment.entity.EnrollmentStatus;
import com.mycompany.project.user.command.domain.aggregate.StudentDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

  // [수정 1] 메서드 이름 변경 (Student -> StudentDetail)
  // 필드명이 studentDetail이므로 ByStudentDetail로 해야 함
  boolean existsByStudentDetailAndCourse(StudentDetail studentDetail, Course course);

  // (필요시) 특정 학생의 신청 취소를 위해 조회
  Optional<Enrollment> findByStudentAndCourse(User student, Course course);
}
  // [수정 2] 메서드 이름 변경 (Student -> StudentDetail)
  Optional<Enrollment> findByStudentDetailAndCourse(StudentDetail studentDetail, Course course);

  // 강좌별 전체 수강신청 목록
  @Query("select e from Enrollment e where e.course.courseId = :courseId")
  List<Enrollment> findByCourseId(@Param("courseId") Long courseId);

  // 강좌 + 상태로 수강신청 목록 조회
  @Query("""
        select e
        from Enrollment e
        where e.course.courseId = :courseId
          and e.status = :status
    """)
  List<Enrollment> findByCourseIdAndStatus(@Param("courseId") Long courseId,
                                           @Param("status") EnrollmentStatus status);

  // 여러 강좌 + 상태로 조회 (일괄 조회용)
  @Query("""
        select e
        from Enrollment e
        where e.course.courseId in :courseIds
          and e.status = :status
    """)
  List<Enrollment> findByCourseIdInAndStatus(@Param("courseIds") List<Long> courseIds,
                                             @Param("status") EnrollmentStatus status);

  // [수정 3] JPQL 경로 수정 (studentDetail.user.userId)
  // 시간표 중복 체크
  @Query("""
        select count(e) > 0
        from Enrollment e
        join e.course c
        join c.timeSlots ts
        where e.studentDetail.user.userId = :userId
          and e.status = 'APPLIED'
          and ts.dayOfWeek = :dayOfWeek
          and ts.period = :period
    """)
  boolean existsTimeConflict(@Param("userId") Long userId,
                             @Param("dayOfWeek") String dayOfWeek,
                             @Param("period") Integer period);
}
