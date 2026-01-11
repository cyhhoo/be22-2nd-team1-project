package com.mycompany.project.attendance.dto.request;

import com.mycompany.project.attendance.entity.enums.CorrectionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 정정요청 목록 조회 조건 DTO
 * - status (PENDING / APPROVED / REJECTED)로 필터링 하는 경우가 많음
 * - 날짜는 보통 request_at 기준으로 검색함.
 */

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
// 정정요청 목록/ 검색
public class CorrectionSearchRequest {

    // 특정 출결(attendance) 기준으로 정정요청 보기 (선택)
    private Long attendanceId;

    // 요청자(교사) 기준 필터
    private Long requestedBy;

    // 처리 상태 필터(선택)
    private CorrectionStatus status;

    // 요청일시 시작(선택)
    private LocalDateTime fromDateTime;

    // 요청일시 끝(선택)
    private LocalDateTime toDateTime;

    // 페이지 크기!!
    private Integer limit;

    // 페이징!!!(선택)
    private Integer offset;


}
