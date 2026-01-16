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
  private final com.mycompany.project.schedule.command.domain.repository.AcademicYearRepository academicYearRepository;

  // Monthly schedule lookup (using MyBatis)
  public List<ScheduleDTO> getMonthlySchedules(int year, int month) {
    return scheduleMapper.selectMonthlySchedules(year, month);
  }

  // Weekly schedule lookup
  public List<ScheduleDTO> getWeeklySchedules(LocalDate startDate, LocalDate endDate) {
    return scheduleMapper.selectWeeklySchedules(startDate, endDate);
  }

  // Internal API for other services to get academic year info
  public com.mycompany.project.schedule.query.dto.InternalAcademicYearResponse getInternalAcademicYear(
      Long academicYearId) {
    return academicYearRepository.findById(java.util.Objects.requireNonNull(academicYearId))
        .map(ay -> {
          com.mycompany.project.schedule.query.dto.InternalAcademicYearResponse res = new com.mycompany.project.schedule.query.dto.InternalAcademicYearResponse();
          res.setAcademicYearId(ay.getAcademicYearId());
          res.setName(ay.getYear() + "-" + ay.getSemester());
          res.setStartDate(ay.getStartDate());
          res.setEndDate(ay.getEndDate());
          return res;
        })
        .orElseThrow(() -> new com.mycompany.project.exception.BusinessException(
            com.mycompany.project.exception.ErrorCode.ACADEMIC_TERM_INFO_MISSING));
  }
}