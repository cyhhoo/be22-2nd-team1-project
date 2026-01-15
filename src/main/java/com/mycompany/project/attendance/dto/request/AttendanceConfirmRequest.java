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
public class AttendanceConfirmRequest {

    // 확정 대상 강좌 ID
    private Long courseId;

    // 확정할 수업 날짜
    private LocalDate classDate;

    // 확정할 교시 (1~8)
    private Integer period;

    // 확정 처리자 ID (담임 또는 권한 있는 교사)
    private Long userId;
}
