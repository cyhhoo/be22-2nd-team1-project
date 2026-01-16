package com.mycompany.project.attendance.command.application.service;

import com.mycompany.project.attendance.command.application.dto.CorrectionCreateRequest;
import com.mycompany.project.attendance.command.application.dto.CorrectionDecideRequest;
import com.mycompany.project.attendance.command.domain.aggregate.Attendance;
import com.mycompany.project.attendance.command.domain.aggregate.AttendanceCorrectionRequest;
import com.mycompany.project.attendance.command.domain.repository.AttendanceCorrectionRequestRepository;
import com.mycompany.project.attendance.command.domain.repository.AttendanceRepository;
import com.mycompany.project.exception.BusinessException;
import com.mycompany.project.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AttendanceCorrectionCommandService {

    private final AttendanceCorrectionRequestRepository correctionRequestRepository;
    private final AttendanceRepository attendanceRepository;

    /**
     * Create attendance correction request
     */
    @Transactional
    public void createCorrectionRequest(CorrectionCreateRequest request) {
        Attendance attendance = attendanceRepository
                .findById(java.util.Objects.requireNonNull(request.getAttendanceId()))
                .orElseThrow(() -> new BusinessException(ErrorCode.ATTENDANCE_NOT_FOUND));

        AttendanceCorrectionRequest correctionRequest = AttendanceCorrectionRequest.builder()
                .attendanceId(request.getAttendanceId())
                .beforeAttendanceCodeId(attendance.getAttendanceCodeId())
                .requestedAttendanceCodeId(request.getRequestedAttendanceCodeId())
                .requestReason(request.getRequestReason())
                .requestedBy(request.getRequestedBy())
                .build();

        correctionRequestRepository.save(java.util.Objects.requireNonNull(correctionRequest));
    }

    /**
     * Approve or reject correction request
     */
    @Transactional
    public void decideCorrectionRequest(CorrectionDecideRequest request) {
        AttendanceCorrectionRequest correctionRequest = correctionRequestRepository
                .findById(java.util.Objects.requireNonNull(request.getRequestId()))
                .orElseThrow(() -> new BusinessException(ErrorCode.CORRECTION_REQUEST_NOT_FOUND));

        if (request.isApproved()) {
            correctionRequest.approve(request.getAdminId(), request.getAdminComment());

            // Apply change to actual attendance
            Attendance attendance = attendanceRepository
                    .findById(java.util.Objects.requireNonNull(correctionRequest.getAttendanceId()))
                    .orElseThrow(() -> new BusinessException(ErrorCode.ATTENDANCE_NOT_FOUND));

            attendance.correctionByAdmin(request.getAdminId(), correctionRequest.getRequestedAttendanceCodeId());
            attendanceRepository.save(java.util.Objects.requireNonNull(attendance));
        } else {
            correctionRequest.reject(request.getAdminId(), request.getAdminComment());
        }

        correctionRequestRepository.save(java.util.Objects.requireNonNull(correctionRequest));
    }
}
