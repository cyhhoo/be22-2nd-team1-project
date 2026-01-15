package com.mycompany.project.schedule.command.application.controller;

import com.mycompany.project.common.response.ApiResponse;
import com.mycompany.project.jwtsecurity.CustomUserDetails;
import com.mycompany.project.schedule.command.application.dto.AcademicYearDTO;
import com.mycompany.project.schedule.command.application.dto.ScheduleCreateRequest;
import com.mycompany.project.schedule.command.application.dto.ScheduleUpdateRequest;
import com.mycompany.project.schedule.command.application.service.ScheduleCommandService;
import com.mycompany.project.schedule.command.domain.aggregate.AcademicSchedule;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/schedule")
@RequiredArgsConstructor
public class ScheduleCommandController {

  private final ScheduleCommandService scheduleCommandService;

  // 1. 학년도 등록
  @PostMapping("/years")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<Long>> createAcademicYear(@RequestBody AcademicYearDTO request) {
    return ResponseEntity.ok(ApiResponse.success(scheduleCommandService.createAcademicYear(request)));
  }

  // 2. 일정 등록
  @PostMapping("/events")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<Long>> createSchedule(
      @RequestBody ScheduleCreateRequest request) {

    return ResponseEntity.ok(ApiResponse.success(scheduleCommandService.createSchedule(request)));
  }

}