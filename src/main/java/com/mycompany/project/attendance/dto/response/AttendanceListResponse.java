package com.mycompany.project.attendance.dto.response;

import com.mycompany.project.attendance.entity.enums.AttendanceState;
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
public class AttendanceListResponse {
    private Long attendanceId;
    private LocalDate classDate;
    private Integer period;
    private AttendanceState state;
    private Long attendanceCodeId;
    private String attendanceCodeName;
    private Long enrollmentId;
    private LocalDateTime confirmedAt;
    private LocalDateTime closedAt;
}
