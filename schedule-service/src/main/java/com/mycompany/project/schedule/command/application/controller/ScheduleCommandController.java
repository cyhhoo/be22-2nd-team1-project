package com.mycompany.project.schedule.command.application.controller;

import com.mycompany.project.common.response.ApiResponse;
import com.mycompany.project.schedule.command.application.dto.AcademicYearDTO;
import com.mycompany.project.schedule.command.application.dto.ScheduleCreateRequest;
import com.mycompany.project.schedule.command.application.service.ScheduleCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "학사 일정 관리 (Command)", description = "학년도 및 상세 일정 등록/수정/삭제 API")
@RestController
@RequestMapping("/api/v1/schedule")
@RequiredArgsConstructor
public class ScheduleCommandController {

  private final ScheduleCommandService scheduleCommandService;

  @Operation(summary = "학년도 등록", description = "새로운 학년도 및 학기를 등록합니다.")
  @PostMapping("/years")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<Long>> createAcademicYear(@RequestBody AcademicYearDTO request) {
    return ResponseEntity.ok(ApiResponse.success(scheduleCommandService.createAcademicYear(request)));
  }

  @Operation(summary = "일정 등록", description = "새로운 학사 일정을 등록합니다.")
  @PostMapping("/events")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<Long>> createSchedule(
      @RequestBody ScheduleCreateRequest request) {
    return ResponseEntity.ok(ApiResponse.success(scheduleCommandService.createSchedule(request)));
  }

  @Operation(summary = "일정 수정", description = "기존 학사 일정을 수정합니다.")
  @PutMapping("/events/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<String>> updateSchedule(
      @PathVariable Long id,
      @RequestBody ScheduleCreateRequest request) {
    scheduleCommandService.updateSchedule(id, request);
    return ResponseEntity.ok(ApiResponse.success("일정 수정 완료"));
  }

  @Operation(summary = "일정 삭제", description = "학사 일정을 삭제(Soft Delete)합니다.")
  @DeleteMapping("/events/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<String>> deleteSchedule(@PathVariable Long id) {
    scheduleCommandService.deleteSchedule(id);
    return ResponseEntity.ok(ApiResponse.success("일정 삭제 완료"));
  }
}
