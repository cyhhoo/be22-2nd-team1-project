package com.mycompany.project.attendance.query.application.dto;

import com.mycompany.project.attendance.command.domain.aggregate.enums.AttendanceState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceResponse {
    private Long attendanceId;
    private LocalDate classDate;
    private Integer period;
    private String reason;
    private AttendanceState state;
    private Long savedBy;
    private LocalDateTime savedAt;
    private Long confirmedBy;
    private LocalDateTime confirmedAt;
    private LocalDateTime closedAt;
    private Long attendanceCodeId;
    private String attendanceCode;
    private String attendanceCodeName;
    private Long enrollmentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
