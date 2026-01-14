package com.mycompany.project.attendance.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceUpdateItemRequest {
    // 어떤 학생(수강신청) 건의 출결인지 식별하는 ID
    private Long enrollmentId;

    // 적용할 출결 코드 ID (예: 출석/지각/결석 등)
    private Long attendanceCodeId;

    // 사유(선택) - 지각 사유, 병가 사유 같은 메모
    private String reason;
}
