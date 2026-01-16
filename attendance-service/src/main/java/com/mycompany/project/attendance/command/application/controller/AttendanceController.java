package com.mycompany.project.attendance.command.application.controller;

import com.mycompany.project.attendance.command.application.dto.AttendanceConfirmRequest;
import com.mycompany.project.attendance.command.application.dto.AttendanceCreateRequest;
import com.mycompany.project.attendance.command.application.dto.AttendanceSearchRequest;
import com.mycompany.project.attendance.command.application.dto.AttendanceUpdateRequest;
import com.mycompany.project.attendance.query.application.dto.AttendanceListResponse;
import com.mycompany.project.attendance.query.application.dto.AttendanceResponse;
import com.mycompany.project.attendance.command.application.service.AttendanceCommandService;
import com.mycompany.project.attendance.query.application.service.AttendanceQueryService;
import com.mycompany.project.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Attendance Management", description = "Attendance creation and management API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/attendance")
public class AttendanceController {

        private final AttendanceCommandService attendanceCommandService;
        private final AttendanceQueryService attendanceQueryService;

        @Operation(summary = "Auto-generate attendance sheet", description = "Query or auto-generate attendance data for a specific course/date/period")
        @PostMapping
        @PreAuthorize("hasRole('TEACHER')")
        public ResponseEntity<ApiResponse<List<AttendanceListResponse>>> generateAttendanceSheet(
                        @RequestBody AttendanceCreateRequest request) {
                List<AttendanceListResponse> data = attendanceCommandService.generateAttendances(request);
                return ResponseEntity.ok(ApiResponse.success(data));
        }

        @Operation(summary = "Save attendance", description = "Teacher saves or modifies attendance information")
        @PatchMapping
        @PreAuthorize("hasRole('TEACHER')")
        public ResponseEntity<ApiResponse<Void>> saveAttendance(@RequestBody AttendanceUpdateRequest request) {
                attendanceCommandService.saveAttendances(request);
                return ResponseEntity.ok(ApiResponse.success(null));
        }

        @Operation(summary = "Confirm attendance", description = "Homeroom teacher confirms attendance")
        @PostMapping("/confirmations")
        @PreAuthorize("hasRole('TEACHER')")
        public ResponseEntity<ApiResponse<Void>> confirmAttendance(@RequestBody AttendanceConfirmRequest request) {
                attendanceCommandService.confirmAttendances(request);
                return ResponseEntity.ok(ApiResponse.success(null));
        }

        @Operation(summary = "Get attendance detail", description = "Query single attendance detail")
        @GetMapping("/{attendanceId}")
        @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
        public ResponseEntity<ApiResponse<AttendanceResponse>> getAttendance(@PathVariable Long attendanceId) {
                AttendanceResponse data = attendanceQueryService.findById(attendanceId);
                return ResponseEntity.ok(ApiResponse.success(data));
        }

        @Operation(summary = "Search attendance list", description = "Query attendance list by conditions")
        @GetMapping
        @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
        public ResponseEntity<ApiResponse<List<AttendanceListResponse>>> search(
                        @ModelAttribute AttendanceSearchRequest request) {
                List<AttendanceListResponse> data = attendanceQueryService.search(request);
                return ResponseEntity.ok(ApiResponse.success(data));
        }
}
