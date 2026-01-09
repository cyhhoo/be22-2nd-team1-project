package com.mycompany.project.attendance.controller;

import com.mycompany.project.attendance.dto.request.CorrectionCreateRequest;
import com.mycompany.project.attendance.dto.request.CorrectionDecideRequest;
import com.mycompany.project.attendance.dto.request.CorrectionSearchRequest;
import com.mycompany.project.attendance.dto.response.CorrectionResponse;
import com.mycompany.project.attendance.service.AttendanceCorrectionCommandService;
import com.mycompany.project.attendance.service.AttendanceCorrectionQueryService;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/attendance/corrections")
public class AttendanceCorrectionController {

    private final AttendanceCorrectionCommandService attendanceCorrectionCommandService;
    private final AttendanceCorrectionQueryService attendanceCorrectionQueryService;

    @PostMapping
    public void create(@RequestBody CorrectionCreateRequest request) {
        // 확정/마감 출결에 대해서만 정정요청을 생성한다.
        attendanceCorrectionCommandService.createCorrectionRequest(request);
    }

    @PostMapping("/decide")
    public void decide(@RequestBody CorrectionDecideRequest request) {
        // 승인 시 출결을 즉시 반영하고, 반려 시 사유가 필요하다.
        attendanceCorrectionCommandService.decideCorrectionRequest(request);
    }

    @GetMapping("/{requestId}")
    public CorrectionResponse findById(@PathVariable Long requestId) {
        return attendanceCorrectionQueryService.findById(requestId);
    }

    @GetMapping
    public List<CorrectionResponse> search(@ModelAttribute CorrectionSearchRequest request) {
        return attendanceCorrectionQueryService.search(request);
    }
}
