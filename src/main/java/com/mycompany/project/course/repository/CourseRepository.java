package com.mycompany.project.course.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mycompany.project.course.entity.Course;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByAcademicYearId(Long academicYearId);
}
