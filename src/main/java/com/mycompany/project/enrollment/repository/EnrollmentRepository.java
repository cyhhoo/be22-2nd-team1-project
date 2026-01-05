package com.mycompany.project.enrollment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mycompany.project.enrollment.entity.Enrollment;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
}
