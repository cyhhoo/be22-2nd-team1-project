package com.mycompany.project.attendance.dto.response;

import com.mycompany.project.attendance.entity.enums.AttendanceState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;


/**
 * 출결 단건(상세) 조회 시 내려주는 응답 DTO
 * - Attendance(출결) 테이블의 주요 컬럼 + AttendanceCode(출결코드) 정보를 함께 전달한다.
 * - 화면에서 "한 건 상세보기" 또는 디버깅/관리 화면에서 상세 정보를 보여줄 때 사용
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceResponse {

    // 출결 PK
    private Long attendanceId;

    // 수업일자
    private LocalDate classDate;

    // 교시(1~8)
    private Integer period;

    // 사유(선택) - 지각/병가 등 메모
    private String reason;

    // 출결 상태 (SAVED / CONFIRMED / CLOSED)
    private AttendanceState state;

    // 저장 처리자 ID (과목 담당교사 userId)
    private Long savedBy;

    // 저장 시각
    private LocalDateTime savedAt;

    // 확정 처리자 ID (담임/권한 있는 교사 userId)
    private Long confirmedBy;

    // 확정 시각 (확정 전이면 null)
    private LocalDateTime confirmedAt;

    // 마감 시각 (마감 전이면 null)
    private LocalDateTime closedAt;

    // 출결 코드 PK
    private Long attendanceCodeId;

    // 출결 코드 값(시스템용) (예: PRESENT, LATE, ABSENT)
    private String attendanceCode;

    // 출결 코드 이름(표시용) (예: 출석, 지각, 결석)
    private String attendanceCodeName;

    // 수강신청 ID (어떤 학생-과목(enrollment)의 출결인지)
    private Long enrollmentId;

    // 출결 데이터 생성 시각
    private LocalDateTime createdAt;

    // 출결 데이터 수정 시각
    private LocalDateTime updatedAt;
}