package com.mycompany.project.course.repository;

import com.mycompany.project.course.entity.CourseTimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseTimeSlotRepository extends JpaRepository<CourseTimeSlot, Long> {
}