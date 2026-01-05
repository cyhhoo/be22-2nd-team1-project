package com.mycompany.project.attendance.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "출결 관리 (Attendance)", description = "출석부 생성 및 마감 관리 API")
@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    /*
     * [구현 가이드: Service 의존성 주입]
     * private final AttendanceService attendanceService;
     */

    @Operation(summary = "출석부 자동 생성 (조회 겸 생성)", description = "특정 강좌의 해당 날짜/교시 출석 데이터를 조회하거나 없으면 자동 생성합니다.")
    @PostMapping("/generate/{courseId}")
    public void generateAttendanceSheet(@PathVariable Long courseId) {
        /*
         * [구현 가이드: 출석부 생성 로직 순서]
         * 1. 요청받은 courseId와 날짜(오늘 등), 교시 정보를 확인합니다.
         * 2. DB 조회: 해당 날짜/교시에 이미 생성된 출결 데이터가 있는지 확인합니다.
         * 3. 데이터가 존재한다면:
         * - 이미 생성된 리스트를 반환합니다.
         * 4. 데이터가 없다면 (최초 조회 시):
         * - 해당 강좌를 수강하는 모든 학생 목록을 조회합니다 (Enrollment 테이블 활용).
         * - 각 학생에 대해 기본 상태값(예: PRESENT-출석)을 가진 Attendance 객체를 메모리에 생성합니다.
         * - JPA saveAll() 또는 MyBatis Bulk Insert를 사용하여 DB에 일괄 저장합니다. (성능 고려)
         * - 저장된 데이터를 반환합니다.
         */
    }

    @Operation(summary = "출결 마감", description = "교사가 해당 수업의 출결 상태를 '마감' 처리합니다.")
    @PostMapping("/close/{attendanceLogId}")
    public void closeAttendance(@PathVariable Long attendanceLogId) {
        /*
         * [구현 가이드: 출결 마감 로직 (상태 머신)]
         * 1. 해당 수업의 출결 로그 또는 관리 데이터(AttendanceLog)를 조회합니다.
         * 2. 권한 체크: 요청자가 해당 강좌의 담당 교사인지 확인합니다.
         * 3. 상태 변경:
         * - 현재 상태를 확인하고, '마감(CLOSED)' 상태로 업데이트합니다.
         * 4. 마감 이후 정책:
         * - 마감 상태가 된 이후에는 일반 교사가 출결을 수정할 수 없도록, 수정 API 등에서 상태 체크 로직이 필요합니다.
         * - 관리자(ADMIN) 권한으로만 수정 가능하도록 제한합니다.
         */
    }
}
