package com.mycompany.project.attendance.dto.response;

import com.mycompany.project.attendance.entity.enums.AttendanceState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 출결 목록 조회(출석부 화면)에서 한 줄(row)씩 내려주는 응답 DTO
 * - 특정 날짜/교시/강좌의 학생별 출결 상태를 화면에 뿌릴 때 사용
 * - Attendance(출결) + AttendanceCode(출결코드) 정보를 합쳐서 전달하는 용도
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceListResponse {

    // 출결 PK (출결 단건을 식별하는 ID)
    private Long attendanceId;

    // 수업일자
    private LocalDate classDate;

    // 교시(1~8)
    private Integer period;

    // 출결 상태 (SAVED / CONFIRMED / CLOSED)
    private AttendanceState state;

    // 출결 코드 ID (예: PRESENT, LATE 같은 코드의 PK)
    private Long attendanceCodeId;

    // 출결 코드 이름(표시용) (예: 출석, 지각, 결석)
    private String attendanceCodeName;

    // 수강신청 ID (어떤 학생-과목(enrollment)의 출결인지)
    private Long enrollmentId;

    // 확정된 시각 (확정 전이면 null)
    private LocalDateTime confirmedAt;

    // 마감된 시각 (마감 전이면 null)
    private LocalDateTime closedAt;
}