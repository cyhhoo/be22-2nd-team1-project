package com.mycompany.project.course.repository;

import com.mycompany.project.course.entity.CourseChangeRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseChangeRequestRepository extends JpaRepository<CourseChangeRequest, Long> {
}
