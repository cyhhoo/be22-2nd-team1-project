package com.mycompany.project.attendance.controller;

import com.mycompany.project.attendance.dto.request.AttendanceConfirmRequest;
import com.mycompany.project.attendance.dto.request.AttendanceCreateRequest;
import com.mycompany.project.attendance.dto.request.AttendanceSearchRequest;
import com.mycompany.project.attendance.dto.request.AttendanceUpdateRequest;
import com.mycompany.project.attendance.dto.response.AttendanceListResponse;
import com.mycompany.project.attendance.dto.response.AttendanceResponse;
import com.mycompany.project.attendance.service.AttendanceCommandService;
import com.mycompany.project.attendance.service.AttendanceQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "출결 관리 (Attendance)", description = "출석부 생성 및 마감 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceCommandService attendanceCommandService;
    private final AttendanceQueryService attendanceQueryService;

    @Operation(summary = "출석부 자동 생성 (조회 겸 생성)", description = "특정 강좌의 해당 날짜/교시 출석 데이터를 조회하거나 없으면 자동 생성합니다.")
    @PostMapping("/generate")
    public java.util.List<AttendanceListResponse> generateAttendanceSheet(@RequestBody AttendanceCreateRequest request) {
        // 출결 데이터가 없으면 생성하고, 있으면 조회 결과만 반환한다.
        return attendanceCommandService.generateAttendances(request);
    }

    @Operation(summary = "출결 저장(등록/수정)", description = "교사가 출결 정보를 저장하거나 수정합니다.")
    @PostMapping("/save")
    public void saveAttendance(@RequestBody AttendanceUpdateRequest request) {
        // 저장은 SAVED 상태로 기록되며, 확정/마감 건은 차단된다.
        attendanceCommandService.saveAttendances(request);
    }

    @Operation(summary = "출결 확정", description = "담임/책임교사가 출결을 확정합니다.")
    @PostMapping("/confirm")
    public void confirmAttendance(@RequestBody AttendanceConfirmRequest request) {
        // 미입력 출결이 있으면 확정 불가.
        attendanceCommandService.confirmAttendances(request);
    }

    @Operation(summary = "출결 상세 조회", description = "출결 단건 상세를 조회합니다.")
    @GetMapping("/{attendanceId}")
    public AttendanceResponse getAttendance(@PathVariable Long attendanceId) {
        return attendanceQueryService.findById(attendanceId);
    }

    @Operation(summary = "출결 목록 조회", description = "조건에 따라 출결 목록을 조회합니다.")
    @GetMapping
    public java.util.List<AttendanceListResponse> search(@ModelAttribute AttendanceSearchRequest request) {
        return attendanceQueryService.search(request);
    }
}
