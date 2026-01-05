package com.mycompany.project.schedule.service;

import com.mycompany.project.schedule.dto.AcademicYearDTO;
import com.mycompany.project.schedule.dto.ScheduleRequest;
import com.mycompany.project.schedule.dto.ScheduleResponse;
import com.mycompany.project.schedule.entity.AcademicSchedule;
import com.mycompany.project.schedule.entity.AcademicYear;
import com.mycompany.project.schedule.repository.AcademicScheduleRepository;
import com.mycompany.project.schedule.repository.AcademicYearRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final AcademicYearRepository yearRepository;
    private final AcademicScheduleRepository scheduleRepository;

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
        return yearRepository.save(academicYear).getAcademicYearId();
    }

    // 2. 상세 일정 등록 (관리자용)
    @Transactional
    public Long createSchedule(ScheduleRequest request) {
        AcademicYear academicYear = yearRepository.findById(request.getAcademicYearId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 학년도입니다."));

        AcademicSchedule schedule = AcademicSchedule.builder()
                .academicYear(academicYear)
                .scheduleDate(request.getScheduleDate())
                .scheduleType(request.getScheduleType())
                .content(request.getContent())
                .build();
        
        return scheduleRepository.save(schedule).getScheduleId();
    }

    // 3. 월별 일정 조회 (누구나 가능)
    @Transactional(readOnly = true)
    public List<ScheduleResponse> getMonthlySchedules(int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        return scheduleRepository.findAllByScheduleDateBetween(start, end).stream()
                .map(ScheduleResponse::new)
                .collect(Collectors.toList());
    }
}