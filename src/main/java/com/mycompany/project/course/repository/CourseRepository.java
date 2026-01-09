package com.mycompany.project.course.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mycompany.project.course.entity.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
