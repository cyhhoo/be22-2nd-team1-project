package com.mycompany.project.schedule.command.service;


import com.mycompany.project.schedule.command.dto.AcademicYearDTO;
import com.mycompany.project.schedule.command.dto.ScheduleRequest;
import com.mycompany.project.schedule.entity.AcademicSchedule;
import com.mycompany.project.schedule.entity.AcademicYear;
import com.mycompany.project.schedule.repository.AcademicScheduleRepository;
import com.mycompany.project.schedule.repository.AcademicYearRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ScheduleCommandService {

  private final AcademicScheduleRepository academicScheduleRepository;
  private final AcademicYearRepository academicYearRepository;

  public ScheduleCommandService(AcademicScheduleRepository academicScheduleRepository, AcademicYearRepository academicYearRepository) {
    this.academicScheduleRepository = academicScheduleRepository;
    this.academicYearRepository = academicYearRepository;
  }

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
  public Long createSchedule(ScheduleRequest request) {
    AcademicYear academicYear = academicYearRepository.findById(request.getAcademicYearId())
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 학년도입니다."));

    AcademicSchedule schedule = AcademicSchedule.builder()
        .academicYear(academicYear)
        .scheduleDate(request.getScheduleDate())
        .scheduleType(request.getScheduleType())
        .content(request.getContent())
        .build();

    return academicScheduleRepository.save(schedule).getScheduleId();
  }
}
