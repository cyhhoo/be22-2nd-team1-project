package com.mycompany.project.schedule.repository;

import com.mycompany.project.schedule.entity.AcademicYear;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AcademicYearRepository extends JpaRepository<AcademicYear, Long> {
    // 1. 특정 연도와 학기로 조회 (예: 2025년 1학기 있는지 확인)
    Optional<AcademicYear> findByYearAndSemester(Integer year, Integer semester);

    // 2. 현재 활성화된 학기 조회
    Optional<AcademicYear> findByIsCurrentTrue();
}