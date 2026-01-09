package com.mycompany.project.attendance.repository;

import com.mycompany.project.attendance.entity.AttendanceCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AttendanceCodeRepository extends JpaRepository<AttendanceCode, Long> {
    Optional<AttendanceCode> findByCodeAndActiveTrue(String code);
}
