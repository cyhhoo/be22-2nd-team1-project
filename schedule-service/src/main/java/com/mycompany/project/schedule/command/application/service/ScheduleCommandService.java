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

  // 1. 학년도/학기 생성 (관리자용)
  @Transactional
  public Long createAcademicYear(AcademicYearDTO request) {
    AcademicYear academicYear = AcademicYear.builder()
        .year(request.getYear())
        .semester(request.getSemester())
        .startDate(request.getStartDate())
        .endDate(request.getEndDate())
        .isCurrent(true) // 새로 만들면 일단 현재 학기로 설정 (로직에 따라 변경 가능)
        .build();
    return academicYearRepository.save(academicYear).getAcademicYearId();
  }

  // 2. 상세 일정 등록 (관리자용)
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

  // 3. 일정 수정
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

  // 4. 일정 삭제 (Soft Delete)
  @Transactional
  public void deleteSchedule(Long scheduleId) {
    AcademicSchedule schedule = academicScheduleRepository.findById(scheduleId)
        .orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND));

    // Soft Delete: 상태만 변경
    schedule.delete();
  }
}
