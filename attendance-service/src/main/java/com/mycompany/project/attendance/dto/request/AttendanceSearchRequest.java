package com.mycompany.project.attendance.dto.request;

import com.mycompany.project.attendance.entity.enums.AttendanceState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 출결 목록 조회 조건 DTO
 * - 검색 필터 화면에서 넘어오는 값들을 담는다.
 * - limit / offset은 페이징 할 때만 사용하고 안쓰면 null
 */

@Builder // 빌드 패턴
@Getter // getter 메서드 자동생성!!
@NoArgsConstructor // 파라미터 없는 생성자 자동생성!!!!
@AllArgsConstructor // 모든 필드에 맞는 매개변수 들어있는 생성자 자동생성!!!!!!!
public class AttendanceSearchRequest {

    // 조회 시작일(포함)
    private LocalDate fromDate;

    // 조회 종료일(포함)
    private LocalDate toDate;

    // 특정 수강 기준 조회 (선택)
    private Long enrollmentId;

    // 과목 기준 조회 (선택)
    private Long courseId;

    // 출결 종류의 코드 필터(선택)
    private Long attendanceCodeId;

    // SAVED / CONFIRMED / CLOSED 필터(선택)
    private AttendanceState state;

    // 교시 필터(선택)
    private Integer period;

    // 페이지 크기 (선택)
    private Integer limit;

    // 시작 위ㅏ치 (선택)
    private Integer offset;

    // 내부 로직용: CourseId 검색 시 EnrollmentId 목록으로 변환하여 사용
    private java.util.List<Long> enrollmentIds;

    public void setEnrollmentIds(java.util.List<Long> enrollmentIds) {
        this.enrollmentIds = enrollmentIds;
    }
}
