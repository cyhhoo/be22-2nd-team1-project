package com.mycompany.project.attendance.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
// 정정요청 승인/반려 처리에 필요한 값들
public class CorrectionDecideRequest {

    // 처리할 정정요청 ID
    private Long requestId;

    // 승인 여부 (true=승인, false=반려)
    private boolean approved;

    // 관리자 코멘트 (반려 시 사유로 사용)
    private String adminComment;

    // 처리자 ID (관리자 userId)
    private Long adminId;
}
