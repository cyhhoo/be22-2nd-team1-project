package com.mycompany.project.attendance.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
// 정정요청 생성에 필요한 값들
public class CorrectionCreateRequest {
    // 정정할 출결 ID
    private Long attendanceId;

    // 변경 요청하는 출결 코드 ID (예: 결석 -> 출석)
    private Long requestedAttendanceCodeId;

    // 정정 요청 사유
    private String requestReason;

    // 정정 요청자 ID (과목 담당교사 userId)
    private Long requestedBy;
}
