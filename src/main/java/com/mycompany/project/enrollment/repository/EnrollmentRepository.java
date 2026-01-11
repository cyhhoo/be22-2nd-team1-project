package com.mycompany.project.enrollment.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.mycompany.project.enrollment.entity.Enrollment;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByCourseId(Long courseId);
}
