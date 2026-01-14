package com.mycompany.project.schedule.query.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class InternalAcademicYearResponse {
    private Long academicYearId;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
}
