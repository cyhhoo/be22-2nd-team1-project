package com.mycompany.project.attendance.entity.enums;

/**
 * 출결 상태 Enum
 * - DB의 ENUM('SAVED','CONFIRMED','CLOSED')와 문자열로 매핑하기 위해 사용
 * - EnumType.STRING으로 저장하면 값이 추가/순서 변경돼도 DB 값이 깨질 위험이 적음(ORDINAL보다 안전)
 */
public enum AttendanceState {
    SAVED,      // 임시 저장(교사가 저장만 한 상태)
    CONFIRMED,  // 확정(교사가 확정한 상태)
    CLOSED      // 마감(더 이상 변경 불가)
}
