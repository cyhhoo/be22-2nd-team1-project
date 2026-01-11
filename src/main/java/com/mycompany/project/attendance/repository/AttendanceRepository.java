package com.mycompany.project.attendance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mycompany.project.attendance.entity.Attendance;
import com.mycompany.project.attendance.entity.Attendance.AttendanceStatus;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    long countByEnrollmentIdAndStatus(Long enrollmentId, AttendanceStatus status);
}
