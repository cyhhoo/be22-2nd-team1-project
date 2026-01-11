package com.mycompany.project.attendance.entity.enums;

/**
 * 정정요청 처리 상태
 * - PENDING  : 처리 대기(요청 생성 직후 기본값)
 * - APPROVED : 관리자 승인(출결에 변경 내용이 반영됨)
 * - REJECTED : 관리자 반려(사유(adminComment)가 같이 남는 경우가 많음)
 */
public enum CorrectionStatus {
    PENDING,
    APPROVED,
    REJECTED
}