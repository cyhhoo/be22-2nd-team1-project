package com.mycompany.project.schedule.query.service;
import com.mycompany.project.schedule.query.dto.ScheduleDTO;
import com.mycompany.project.schedule.query.mapper.ScheduleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleQueryService {
  private final ScheduleMapper scheduleMapper;

  // 월간 일정 조회 (MyBatis 사용)
  public List<ScheduleDTO> getMonthlySchedules(int year, int month) {
    return scheduleMapper.selectMonthlySchedules(year, month);
  }

  // 주간 일정 조회
  public List<ScheduleDTO> getWeeklySchedules(LocalDate startDate, LocalDate endDate) {
    return scheduleMapper.selectWeeklySchedules(startDate,endDate);
  }
}