package com.mycompany.project.attendance.command.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CorrectionCreateRequest {
    private Long attendanceId;
    private Long requestedAttendanceCodeId;
    private String requestReason;
    private Long requestedBy;
}
