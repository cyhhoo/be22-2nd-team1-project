package com.mycompany.project.attendance.dto.request;

import com.mycompany.project.attendance.entity.enums.ScopeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceClosureRequest {
    private Long academicYearId;
    private ScopeType scopeType;
    private String scopeValue;
    private Integer grade;
    private Integer classNo;
    private Long courseId;
    private Long userId;
}
