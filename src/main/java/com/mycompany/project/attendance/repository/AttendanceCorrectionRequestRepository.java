package com.mycompany.project.attendance.repository;

import com.mycompany.project.attendance.entity.AttendanceCorrectionRequest;
import com.mycompany.project.attendance.entity.enums.CorrectionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 정정요청(AttendanceCorrectionRequest) JPA Repository
 * - 정정요청 생성/승인/반려 같은 "쓰기" 작업에서 사용
 */
public interface AttendanceCorrectionRequestRepository extends JpaRepository<AttendanceCorrectionRequest, Long> {

    /**
     * 동일 출결(attendance_id)에 대해 특정 상태(PENDING 등)의 정정요청이 이미 있는지 확인
     * - 중복 정정요청 생성 방지용
     */
    boolean existsByAttendanceIdAndStatus(Long attendanceId, CorrectionStatus status);
}