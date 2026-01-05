package com.mycompany.project.schedule.repository;

import com.mycompany.project.schedule.entity.AcademicSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AcademicScheduleRepository extends JpaRepository<AcademicSchedule, Long> {
    
    // 1. 특정 학기의 모든 일정 조회
    List<AcademicSchedule> findAllByAcademicYear_AcademicYearId(Long academicYearId);

    // 2. 특정 기간(시작~종료) 사이의 일정 조회 (월별 캘린더용)
    List<AcademicSchedule> findAllByScheduleDateBetween(LocalDate startDate, LocalDate endDate);
}