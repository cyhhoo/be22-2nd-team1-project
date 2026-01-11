package com.mycompany.project.schedule.query.controller;

import com.mycompany.project.common.response.ApiResponse;
import com.mycompany.project.schedule.query.dto.ScheduleResponse;
import com.mycompany.project.schedule.query.service.ScheduleQueryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedule")
public class ScheduleQueryController {

  private final ScheduleQueryService scheduleQueryService;

  public ScheduleQueryController(ScheduleQueryService scheduleQueryService) {
    this.scheduleQueryService = scheduleQueryService;
  }

  // 3. 월별 일정 조회
  @GetMapping("/events")
  public ApiResponse<List<ScheduleResponse>> getMonthlySchedules(
          @RequestParam int year,
          @RequestParam int month) {
      return ApiResponse.success(scheduleQueryService.getMonthlySchedules(year, month));
  }
}