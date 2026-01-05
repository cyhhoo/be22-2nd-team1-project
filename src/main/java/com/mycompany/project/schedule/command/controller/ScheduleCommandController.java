package com.mycompany.project.schedule.command.controller;

import com.mycompany.project.common.dto.ApiDTO;
import com.mycompany.project.schedule.command.dto.AcademicYearDTO;
import com.mycompany.project.schedule.command.dto.ScheduleRequest;
import com.mycompany.project.schedule.command.service.ScheduleCommandService;
import lombok.RequiredArgsConstructor;
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
    public ApiDTO<Long> createAcademicYear(@RequestBody AcademicYearDTO request) {
        return ApiDTO.success(scheduleCommandService.createAcademicYear(request));
    }

    // 2. 일정 등록
    @PostMapping("/events")
    public ApiDTO<Long> createSchedule(@RequestBody ScheduleRequest request) {
        return ApiDTO.success(scheduleCommandService.createSchedule(request));
    }
}