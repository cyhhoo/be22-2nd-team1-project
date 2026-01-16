package com.mycompany.project.attendance.command.application.controller;

import com.mycompany.project.attendance.query.application.dto.AttendanceCodeResponse;
import com.mycompany.project.attendance.query.application.service.AttendanceCodeQueryService;
import com.mycompany.project.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/attendance/codes")
public class AttendanceCodeController {

    private final AttendanceCodeQueryService attendanceCodeQueryService;

    @GetMapping("/{attendanceCodeId}")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<ApiResponse<AttendanceCodeResponse>> findById(@PathVariable Long attendanceCodeId) {
        AttendanceCodeResponse data = attendanceCodeQueryService.findById(attendanceCodeId);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN','STUDENT')")
    public ResponseEntity<ApiResponse<List<AttendanceCodeResponse>>> findAll(
            @RequestParam(required = false) Boolean activeOnly) {
        List<AttendanceCodeResponse> data = attendanceCodeQueryService.findAll(activeOnly);
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}
