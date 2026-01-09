package com.mycompany.project.attendance.repository;

import com.mycompany.project.attendance.entity.AttendanceCorrectionRequest;
import com.mycompany.project.attendance.entity.enums.CorrectionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceCorrectionRequestRepository extends JpaRepository<AttendanceCorrectionRequest, Long> {
    boolean existsByAttendanceIdAndStatus(Long attendanceId, CorrectionStatus status);
}
