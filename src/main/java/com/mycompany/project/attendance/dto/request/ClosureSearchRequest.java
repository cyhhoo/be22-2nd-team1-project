package com.mycompany.project.attendance.dto.request;


import com.mycompany.project.attendance.entity.enums.ScopeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 마감이력 목록 조회 조건 DTO
 * - scopeType + scopeValue 조합이 핵심(MONTH/SEMESTER)
 * - grade/classNo/courseId는 "부분 마감" 같은 케이스에서만 들어옴 (선택)
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class ClosureSearchRequest {

        // 학년, 학기 FK
        private Long academicYearId;

        // MONTH or SEMESTER
        private ScopeType scopeType;

        // 예: 2025-09 / 2025-1
        private String scopeValue;

        // 학년 (선택)
        private Integer grade;

        // 반 (선택)
        private Integer classNo;

        // 강좌 (선택)
        private Long courseId;

        // 페이지 크기 (선택)
        private Integer limit;

        // 페이징 (선택)
        private Integer offset;


}
