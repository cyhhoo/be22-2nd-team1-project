package com.mycompany.project.attendance.query.application.dto;

import com.mycompany.project.attendance.command.domain.aggregate.enums.CorrectionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CorrectionResponse {
    private Long requestId;
    private Long attendanceId;
    private Long beforeAttendanceCodeId;
    private Long requestedAttendanceCodeId;
    private String requestReason;
    private CorrectionStatus status;
    private Long requestedBy;
    private LocalDateTime requestedAt;
    private Long decidedBy;
    private LocalDateTime decidedAt;
    private String adminComment;
    private Boolean pendingFlag;
}
