package com.mycompany.project.attendance.dto.response;


import com.mycompany.project.attendance.entity.enums.ScopeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 마감 이력 조회 결과
 * - MyBatis SELECT 결과를 담는 용도
 */

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttendanceClosureResponse {

    // 마감자
    private Long closureId;

    // 학년 학기
    private Long academicYearId;

    // Month or SEMESTER
    private ScopeType scopeType;

    // 예시로 2025-01~
    private String scopeValue;

    // 학년
    private Integer grade;

    // 반
    private Integer classNo;

    // 강좌
    private Long courseId;


    private LocalDateTime closedAt;
    private Long userId; // 누가 마감했는지



}
