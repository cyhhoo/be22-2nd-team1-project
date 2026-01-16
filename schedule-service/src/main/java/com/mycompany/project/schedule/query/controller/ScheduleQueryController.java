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

@Tag(name = "Academic Schedule Query", description = "Academic schedule query API")
@RestController
@RequestMapping("/api/v1/schedule")
@RequiredArgsConstructor
public class ScheduleQueryController {

  private final ScheduleQueryService scheduleQueryService;

  @Operation(summary = "Get monthly schedule", description = "Retrieve academic schedule for a specified year and month.")
  @GetMapping("/events/monthly")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResponse<List<ScheduleDTO>>> getMonthlySchedules(
      @RequestParam int year,
      @RequestParam int month) {

    List<ScheduleDTO> schedules = scheduleQueryService.getMonthlySchedules(year, month);

    return ResponseEntity.ok(ApiResponse.success(schedules));

  }

  @Operation(summary = "Get weekly schedule", description = "Retrieve academic schedule between start and end dates.")
  @GetMapping("/events/weekly")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResponse<List<ScheduleDTO>>> getWeeklySchedules(
      @RequestParam LocalDate startDate,
      @RequestParam LocalDate endDate) {

    List<ScheduleDTO> schedules = scheduleQueryService.getWeeklySchedules(startDate, endDate);
    return ResponseEntity.ok(ApiResponse.success(schedules));
  }

  // Internal API for other services to get academic year info
  @Hidden
  @GetMapping("/internal/academic-years/{academicYearId}")
  public ResponseEntity<com.mycompany.project.schedule.query.dto.InternalAcademicYearResponse> getInternalAcademicYear(
      @PathVariable Long academicYearId) {
    return ResponseEntity.ok(scheduleQueryService.getInternalAcademicYear(academicYearId));
  }
}