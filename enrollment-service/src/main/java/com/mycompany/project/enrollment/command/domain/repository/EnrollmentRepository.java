package com.mycompany.project.enrollment.command.domain.repository;

import com.mycompany.project.enrollment.command.domain.aggregate.Enrollment;
import com.mycompany.project.common.enums.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findByCourseIdAndStatus(Long courseId, EnrollmentStatus status);

    boolean existsByStudentDetailIdAndCourseId(Long studentDetailId, Long courseId);

    List<Enrollment> findByCourseId(Long courseId);

    Optional<Enrollment> findByCourseIdAndStudentDetailId(Long courseId, Long studentDetailId);

    @Query("SELECT e.courseId FROM Enrollment e WHERE e.studentDetailId = :studentId AND e.status = 'APPLIED'")
    List<Long> findEnrolledCourseIds(@Param("studentId") Long studentId);
}
