package com.mycompany.project.attendance.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceCreateRequest {
    // 출석부를 생성할 강좌 ID
    private Long courseId;

    // 출석부 생성 기준 날짜(수업일자)
    private LocalDate classDate;

    // 출석부 생성 기준 교시(1~8)
    private Integer period;

    // 생성 요청자 ID(과목 담당교사 userId)
    private Long userId;
}
