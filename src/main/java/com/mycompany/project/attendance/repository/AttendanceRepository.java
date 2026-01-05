package com.mycompany.project.attendance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mycompany.project.attendance.entity.Attendance;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
}
