package com.mycompany.project.enrollment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mycompany.project.enrollment.entity.Enrollment;

import java.util.Collection;
import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByCourseIdAndStatus(Long courseId, String status);

    List<Enrollment> findByCourseIdInAndStatus(Collection<Long> courseIds, String status);
}
