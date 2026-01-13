package com.mycompany.project.schedule.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "tbl_academic_year")
public class AcademicYear {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long academicYearId;

    @Column(name = "year", nullable = false)
    private Integer year; // 2025, 2026...

    @Column(nullable = false)
    private Integer semester; // 1, 2

    private LocalDate startDate; // 학기 시작일
    private LocalDate endDate; // 학기 종료일

    // 현재 진행 중인 학기인지 여부
    // '현재 학기 여부'
    private Boolean isCurrent;
}