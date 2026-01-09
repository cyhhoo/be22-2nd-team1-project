package com.mycompany.project.attendance.repository;

import com.mycompany.project.attendance.entity.AttendanceClosure;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceClosureRepository extends JpaRepository<AttendanceClosure, Long> {
}
