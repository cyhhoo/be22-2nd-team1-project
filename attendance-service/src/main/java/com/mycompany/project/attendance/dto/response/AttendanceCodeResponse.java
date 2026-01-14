package com.mycompany.project.attendance.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

    /**
     * 출결코드 조회 결과 DTO
     * - tbl_attendance_code 테이블 조회 결과를 담는 용도
     * - MyBatis SELECT에서 AS로 필드명 맞추면 자동 매핑된다.
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    // 출결 코드 (출석, 지각, 결석 같은 코드 목록 상세
    public class AttendanceCodeResponse {

        // PK
        private Long attendanceCodeId;

        // 시스템 코드값 (예: PRESENT, LATE ...)
        private String code;

        // 화면 표시명 (예: 출석, 지각 ...)
        private String name;

        // 병가/공가처럼 "참작" 처리 여부
        // DB는 tinyint(1)로 들어오는데 자바 boolean으로 받아도 보통 문제 없다.
        private boolean excused;

        // 코드 사용 여부(삭제 대신 비활성 처리할 때)
        private boolean active;

        // 생성/수정 시간 (DB 컬럼: created_at, updated_at)
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

    }
