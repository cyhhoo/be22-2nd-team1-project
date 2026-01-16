package com.mycompany.project.schedule.command.domain.repository;

import com.mycompany.project.schedule.command.domain.aggregate.AcademicYear;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AcademicYearRepository extends JpaRepository<AcademicYear, Long> {
    // 1. ?뱀젙 ?곕룄? ?숆린濡?議고쉶 (?? 2025??1?숆린 ?덈뒗吏 ?뺤씤)
    Optional<AcademicYear> findByYearAndSemester(Integer year, Integer semester);

    // 2. ?꾩옱 ?쒖꽦?붾맂 ?숆린 議고쉶
    Optional<AcademicYear> findByIsCurrentTrue();
}