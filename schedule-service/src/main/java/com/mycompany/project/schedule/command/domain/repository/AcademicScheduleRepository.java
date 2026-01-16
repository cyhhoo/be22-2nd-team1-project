package com.mycompany.project.schedule.command.domain.repository;

import com.mycompany.project.schedule.command.domain.aggregate.AcademicSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AcademicScheduleRepository extends JpaRepository<AcademicSchedule, Long> {

    // 1. ?뱀젙 ?숆린??紐⑤뱺 ?쇱젙 議고쉶
    List<AcademicSchedule> findAllByAcademicYear_AcademicYearId(Long academicYearId);

    // 2. ?뱀젙 湲곌컙(?쒖옉~醫낅즺) ?ъ씠???쇱젙 議고쉶 (?붾퀎 罹섎┛?붿슜)
    List<AcademicSchedule> findAllByScheduleDateBetween(LocalDate startDate, LocalDate endDate);
}