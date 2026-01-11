package com.mycompany.project.schedule.command.application.controller;

import com.mycompany.project.common.response.ApiResponse;
import com.mycompany.project.schedule.command.application.dto.AcademicYearDTO;
import com.mycompany.project.schedule.command.application.dto.ScheduleRequest;
import com.mycompany.project.schedule.command.application.service.ScheduleCommandService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/schedule")
public class ScheduleCommandController {

  private final ScheduleCommandService scheduleCommandService;

  public ScheduleCommandController(ScheduleCommandService scheduleCommandService) {
    this.scheduleCommandService = scheduleCommandService;
  }

  // 1. 학년도 등록
  @PostMapping("/years")
  public ApiResponse<Long> createAcademicYear(@RequestBody AcademicYearDTO request) {
    return ApiResponse.success(scheduleCommandService.createAcademicYear(request));
  }

  // 2. 일정 등록
  @PostMapping("/events")
  public ApiResponse<Long> createSchedule(@RequestBody ScheduleRequest request) {
    return ApiResponse.success(scheduleCommandService.createSchedule(request));
  }
}