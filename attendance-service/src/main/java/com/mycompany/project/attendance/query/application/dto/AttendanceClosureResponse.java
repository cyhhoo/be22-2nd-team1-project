package com.mycompany.project.attendance.query.application.dto;

import com.mycompany.project.attendance.command.domain.aggregate.enums.ScopeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceClosureResponse {
    private Long closureId;
    private Long academicYearId;
    private ScopeType scopeType;
    private String scopeValue;
    private Integer grade;
    private Integer classNo;
    private Long courseId;
    private LocalDateTime closedAt;
    private Long userId;
}
