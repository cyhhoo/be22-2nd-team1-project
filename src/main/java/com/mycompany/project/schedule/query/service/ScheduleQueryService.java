package com.mycompany.project.schedule.query.service;

import com.mycompany.project.schedule.query.dto.ScheduleResponse;
import com.mycompany.project.schedule.repository.AcademicScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduleQueryService {

  private final AcademicScheduleRepository academicScheduleRepository;

  public ScheduleQueryService(AcademicScheduleRepository academicScheduleRepository) {
    this.academicScheduleRepository = academicScheduleRepository;
  }

  // 3. 월별 일정 조회 (누구나 가능)
  @Transactional(readOnly = true)
  public List<ScheduleResponse> getMonthlySchedules(int year, int month) {
    LocalDate start = LocalDate.of(year, month, 1);
    LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

    return academicScheduleRepository.findAllByScheduleDateBetween(start, end).stream()
        .map(ScheduleResponse::new)
        .collect(Collectors.toList());
  }
}
