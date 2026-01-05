package com.mycompany.project.schedule.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class AcademicYearDTO {
    private Integer year;       // 예: 2025
    private Integer semester;   // 예: 1
    private LocalDate startDate;
    private LocalDate endDate;
}