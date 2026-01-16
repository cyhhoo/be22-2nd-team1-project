package com.mycompany.project.course.command.domain.repository;

import com.mycompany.project.course.command.domain.aggregate.CourseChangeRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseChangeRequestRepository extends JpaRepository<CourseChangeRequest, Long> {
}
