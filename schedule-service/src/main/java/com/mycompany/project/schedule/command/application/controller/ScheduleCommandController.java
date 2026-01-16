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

@Tag(name = "Academic Schedule Management (Command)", description = "Academic year and detailed schedule registration/update/deletion API")
@RestController
@RequestMapping("/api/v1/schedule")
@RequiredArgsConstructor
public class ScheduleCommandController {

  private final ScheduleCommandService scheduleCommandService;

  @Operation(summary = "Register academic year", description = "Register a new academic year and semester.")
  @PostMapping("/years")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<Long>> createAcademicYear(@RequestBody AcademicYearDTO request) {
    return ResponseEntity.ok(ApiResponse.success(scheduleCommandService.createAcademicYear(request)));
  }

  @Operation(summary = "Register schedule event", description = "Register a new academic schedule event.")
  @PostMapping("/events")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<Long>> createSchedule(
      @RequestBody ScheduleCreateRequest request) {
    return ResponseEntity.ok(ApiResponse.success(scheduleCommandService.createSchedule(request)));
  }

  @Operation(summary = "Update schedule event", description = "Modify an existing academic schedule event.")
  @PutMapping("/events/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<String>> updateSchedule(
      @PathVariable Long id,
      @RequestBody ScheduleCreateRequest request) {
    scheduleCommandService.updateSchedule(id, request);
    return ResponseEntity.ok(ApiResponse.success("Schedule updated successfully"));
  }

  @Operation(summary = "Delete schedule event", description = "Delete an academic schedule event (Soft Delete).")
  @DeleteMapping("/events/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<String>> deleteSchedule(@PathVariable Long id) {
    scheduleCommandService.deleteSchedule(id);
    return ResponseEntity.ok(ApiResponse.success("Schedule deleted successfully"));
  }
}
