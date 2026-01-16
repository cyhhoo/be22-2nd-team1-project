package com.mycompany.project.attendance.command.application.controller;

import com.mycompany.project.attendance.command.application.dto.CorrectionCreateRequest;
import com.mycompany.project.attendance.command.application.dto.CorrectionDecideRequest;
import com.mycompany.project.attendance.command.application.dto.CorrectionSearchRequest;
import com.mycompany.project.attendance.query.application.dto.CorrectionResponse;
import com.mycompany.project.attendance.command.application.service.AttendanceCorrectionCommandService;
import com.mycompany.project.attendance.query.application.service.AttendanceCorrectionQueryService;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/attendance/corrections")
public class AttendanceCorrectionController {

    private final AttendanceCorrectionCommandService attendanceCorrectionCommandService;
    private final AttendanceCorrectionQueryService attendanceCorrectionQueryService;

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<Void>> create(@RequestBody CorrectionCreateRequest request) {
        attendanceCorrectionCommandService.createCorrectionRequest(request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PatchMapping("/{requestId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> decide(
            @PathVariable Long requestId,
            @RequestBody CorrectionDecideRequest request) {
        CorrectionDecideRequest normalizedRequest = CorrectionDecideRequest.builder()
                .requestId(requestId)
                .approved(request.isApproved())
                .adminComment(request.getAdminComment())
                .adminId(request.getAdminId())
                .build();
        attendanceCorrectionCommandService.decideCorrectionRequest(normalizedRequest);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/{requestId}")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<ApiResponse<CorrectionResponse>> findById(@PathVariable Long requestId) {
        CorrectionResponse data = attendanceCorrectionQueryService.findById(requestId);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<ApiResponse<List<CorrectionResponse>>> search(
            @ModelAttribute CorrectionSearchRequest request) {
        List<CorrectionResponse> data = attendanceCorrectionQueryService.search(request);
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}
