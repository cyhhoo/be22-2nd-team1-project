package com.mycompany.project.schedule.controller;

import com.mycompany.project.common.dto.ApiDTO;
import com.mycompany.project.schedule.dto.AcademicYearDTO;
import com.mycompany.project.schedule.dto.ScheduleRequest;
import com.mycompany.project.schedule.dto.ScheduleResponse;
import com.mycompany.project.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    // 1. 학년도 등록 (예: 2025-1학기 생성)
    @PostMapping("/years")
    public ApiDTO<Long> createAcademicYear(@RequestBody AcademicYearDTO request) {
        Long yearId = scheduleService.createAcademicYear(request);
        return ApiDTO.success(yearId);
    }

    // 2. 일정 등록 (예: 3/2 개강일)
    @PostMapping("/events")
    public ApiDTO<Long> createSchedule(@RequestBody ScheduleRequest request) {
        Long scheduleId = scheduleService.createSchedule(request);
        return ApiDTO.success(scheduleId);
    }

    // 3. 월별 일정 조회
    @GetMapping("/events")
    public ApiDTO<List<ScheduleResponse>> getMonthlySchedules(
            @RequestParam int year,
            @RequestParam int month) {
        List<ScheduleResponse> schedules = scheduleService.getMonthlySchedules(year, month);
        return ApiDTO.success(schedules);
    }
}