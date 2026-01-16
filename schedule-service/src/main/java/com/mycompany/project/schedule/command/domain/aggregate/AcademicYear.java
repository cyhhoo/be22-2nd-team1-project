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
    private Integer year; // 2025, 2026, etc.

    @Column(nullable = false)
    private Integer semester; // 1, 2

    private LocalDate startDate; // Semester start date
    private LocalDate endDate; // Semester end date

    // Indicates if this is the currently active semester
    private Boolean isCurrent;
}