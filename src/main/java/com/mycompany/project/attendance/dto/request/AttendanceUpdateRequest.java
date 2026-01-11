package com.mycompany.project.attendance.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceUpdateRequest {

    // 저장(등록/수정) 대상 강좌 ID
    private Long courseId;

    // 저장할 수업 날짜
    private LocalDate classDate;

    // 저장할 교시(1~8)
    private Integer period;

    // 저장 처리자 ID(과목 담당교사 userId)
    private Long userId;

    // 학생별 출결 저장 항목 목록
    private List<AttendanceUpdateItemRequest> items;
}
