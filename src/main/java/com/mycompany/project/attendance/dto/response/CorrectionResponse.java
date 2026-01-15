package com.mycompany.project.attendance.dto.response;

import com.mycompany.project.attendance.entity.enums.CorrectionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 정정요청 단건/목록 조회 시 내려주는 응답 DTO
 * - AttendanceCorrectionRequest(정정요청) 정보를 화면에 보여주기 위한 클래스
 * - 요청 내용(변경 전/후 코드, 사유) + 처리 상태(승인/반려/대기) + 처리자/시각을 함께 전달한다.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CorrectionResponse {

    // 정정요청 PK
    private Long requestId;

    // 정정 대상 출결 ID
    private Long attendanceId;

    // 변경 전 출결 코드 ID
    private Long beforeAttendanceCodeId;

    // 변경 요청 출결 코드 ID
    private Long requestedAttendanceCodeId;

    // 정정 요청 사유(교사가 작성)
    private String requestReason;

    // 요청 상태 (PENDING / APPROVED / REJECTED)
    private CorrectionStatus status;

    // 요청자 ID (교사 userId)
    private Long requestedBy;

    // 요청 시각
    private LocalDateTime requestedAt;

    // 처리자 ID (관리자 userId) - 처리 전이면 null
    private Long decidedBy;

    // 처리 시각 - 처리 전이면 null
    private LocalDateTime decidedAt;

    // 관리자 코멘트 (승인/반려 시 남길 수 있음, 반려 시 보통 필수)
    private String adminComment;

    // PENDING 표시용 플래그(선택) - 화면 표시/필터링 목적
    private Boolean pendingFlag;
}