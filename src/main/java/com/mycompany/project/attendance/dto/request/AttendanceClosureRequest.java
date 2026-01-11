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
    // 마감 대상 학년도/학기 ID
    private Long academicYearId;

    // 마감 범위 타입 (MONTH / SEMESTER)
    private ScopeType scopeType;

    // 마감 범위 값 (예: "2026-01" 또는 "2026-1")
    private String scopeValue;

    // 마감 대상 학년(선택)
    private Integer grade;

    // 마감 대상 반(선택)
    private Integer classNo;

    // 마감 대상 강좌 ID(선택)
    private Long courseId;

    // 마감 요청자 ID(관리자 userId)
    private Long userId;
}
