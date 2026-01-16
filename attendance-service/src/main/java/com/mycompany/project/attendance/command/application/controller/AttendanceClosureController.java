package com.mycompany.project.attendance.command.application.controller;

import com.mycompany.project.attendance.command.application.dto.AttendanceClosureRequest;
import com.mycompany.project.attendance.command.application.dto.ClosureSearchRequest;
import com.mycompany.project.attendance.query.application.dto.AttendanceClosureResponse;
import com.mycompany.project.attendance.command.application.service.AttendanceClosureCommandService;
import com.mycompany.project.attendance.query.application.service.AttendanceClosureQueryService;
import com.mycompany.project.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/attendance/closures")
public class AttendanceClosureController {

    private final AttendanceClosureCommandService attendanceClosureCommandService;
    private final AttendanceClosureQueryService attendanceClosureQueryService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> close(@RequestBody AttendanceClosureRequest request) {
        attendanceClosureCommandService.closeAttendances(request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/{closureId}")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<ApiResponse<AttendanceClosureResponse>> findById(@PathVariable Long closureId) {
        AttendanceClosureResponse data = attendanceClosureQueryService.findById(closureId);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<ApiResponse<List<AttendanceClosureResponse>>> search(
            @ModelAttribute ClosureSearchRequest request) {
        List<AttendanceClosureResponse> data = attendanceClosureQueryService.search(request);
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}
