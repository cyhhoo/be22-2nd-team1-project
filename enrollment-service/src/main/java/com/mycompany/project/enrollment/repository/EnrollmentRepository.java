package com.mycompany.project.enrollment.repository;

import com.mycompany.project.enrollment.entity.Enrollment;
import com.mycompany.project.enrollment.entity.EnrollmentStatus;
import com.mycompany.project.user.command.domain.aggregate.StudentDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findByCourseIdAndStatus(Long courseId, EnrollmentStatus status);

    boolean existsByStudentDetailAndCourseId(StudentDetail studentDetail, Long courseId);

    /**
     * 학생이 현재 수강 신청 완료(APPLIED) 상태인 과목 ID 리스트 조회
     */
    @Query("SELECT e.courseId FROM Enrollment e WHERE e.studentDetail.id = :studentId AND e.status = 'APPLIED'")
    List<Long> findEnrolledCourseIds(@Param("studentId") Long studentId);
}
