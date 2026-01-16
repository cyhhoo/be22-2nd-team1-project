package com.mycompany.project.schedule.command.application.service;

import com.mycompany.project.exception.BusinessException;
import com.mycompany.project.exception.ErrorCode;
import com.mycompany.project.schedule.command.application.dto.AcademicYearDTO;
import com.mycompany.project.schedule.command.application.dto.ScheduleCreateRequest;
import com.mycompany.project.schedule.command.domain.aggregate.AcademicSchedule;
import com.mycompany.project.schedule.command.domain.aggregate.AcademicYear;
import com.mycompany.project.schedule.command.domain.repository.AcademicScheduleRepository;
import com.mycompany.project.schedule.command.domain.repository.AcademicYearRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScheduleCommandService {

  private final AcademicScheduleRepository academicScheduleRepository;
  private final AcademicYearRepository academicYearRepository;

  // 1. Create academic year (Admin)
  @Transactional
  public Long createAcademicYear(AcademicYearDTO request) {
    // Duplicate check: Return existing ID if already exists (Idempotent)
    return academicYearRepository.findByYearAndSemester(request.getYear(), request.getSemester())
        .map(AcademicYear::getAcademicYearId)
        .orElseGet(() -> {
          AcademicYear academicYear = AcademicYear.builder()
              .year(request.getYear())
              .semester(request.getSemester())
              .startDate(request.getStartDate())
              .endDate(request.getEndDate())
              .isCurrent(true)
              .build();
          return academicYearRepository.save(academicYear).getAcademicYearId();
        });
  }

  // 2. Register schedule (Admin)
  @Transactional
  public Long createSchedule(ScheduleCreateRequest request) {
    AcademicYear academicYear = academicYearRepository.findById(request.getAcademicYearId())
        .orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_ACADEMIC_YEAR_NOT_FOUND));

    AcademicSchedule schedule = AcademicSchedule.builder()
        .academicYear(academicYear)
        .scheduleDate(request.getScheduleDate())
        .scheduleType(request.getScheduleType())
        .content(request.getContent())
        .targetGrade(request.getTargetGrade())
        .build();

    return academicScheduleRepository.save(schedule).getScheduleId();
  }

  // 3. Update schedule
  @Transactional
  public void updateSchedule(Long scheduleId, ScheduleCreateRequest request) {
    AcademicSchedule schedule = academicScheduleRepository.findById(scheduleId)
        .orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND));

    schedule.update(
        request.getScheduleDate(),
        request.getScheduleType(),
        request.getContent(),
        request.getTargetGrade());
  }

  // 4. Delete schedule (Soft Delete)
  @Transactional
  public void deleteSchedule(Long scheduleId) {
    AcademicSchedule schedule = academicScheduleRepository.findById(scheduleId)
        .orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND));

    // Soft Delete: status change naturally
    schedule.delete();
  }
}
