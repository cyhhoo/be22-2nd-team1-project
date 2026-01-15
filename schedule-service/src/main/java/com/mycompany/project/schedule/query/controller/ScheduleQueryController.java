package com.mycompany.project.schedule.query.controller;

import com.mycompany.project.common.response.ApiResponse;
import com.mycompany.project.schedule.query.dto.ScheduleDTO;
import com.mycompany.project.schedule.query.service.ScheduleQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "학사 일정 조회 (Schedule Query)", description = "학사 일정 조회 API")
@RestController
@RequestMapping("/api/v1/schedule")
@RequiredArgsConstructor
public class ScheduleQueryController {

  private final ScheduleQueryService scheduleQueryService;

  @Operation(summary = "월별 일정 조회", description = "지정한 연도와 월에 해당하는 학사 일정을 조회합니다.")
  @GetMapping("/events/monthly")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResponse<List<ScheduleDTO>>> getMonthlySchedules(
      @RequestParam int year,
      @RequestParam int month) {

    List<ScheduleDTO> schedules = scheduleQueryService.getMonthlySchedules(year, month);

    return ResponseEntity.ok(ApiResponse.success(schedules));

  }

  @Operation(summary = "주간 일정 조회", description = "시작일과 종료일 사이의 학사 일정을 조회합니다.")
  @GetMapping("/events/weekly")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResponse<List<ScheduleDTO>>> getWeeklySchedules(
      @RequestParam LocalDate startDate,
      @RequestParam LocalDate endDate) {

    List<ScheduleDTO> schedules = scheduleQueryService.getWeeklySchedules(startDate, endDate);
    return ResponseEntity.ok(ApiResponse.success(schedules));
  }

  // 내부 API용 학년도 단건 조회
  @Hidden
  @GetMapping("/internal/academic-years/{academicYearId}")
  public ResponseEntity<com.mycompany.project.schedule.query.dto.InternalAcademicYearResponse> getInternalAcademicYear(
      @PathVariable Long academicYearId) {
    return ResponseEntity.ok(scheduleQueryService.getInternalAcademicYear(academicYearId));
  }
}