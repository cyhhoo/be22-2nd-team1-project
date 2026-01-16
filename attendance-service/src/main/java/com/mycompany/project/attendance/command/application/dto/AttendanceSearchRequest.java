package com.mycompany.project.attendance.command.application.dto;

import com.mycompany.project.attendance.command.domain.aggregate.enums.AttendanceState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceSearchRequest {
    private Long courseId;
    private LocalDate fromDate;
    private LocalDate toDate;
    private Integer period;
    private Long enrollmentId;
    private Long attendanceCodeId;
    private AttendanceState state;
    private List<Long> enrollmentIds;
    private Integer limit;
    private Integer offset;
}
