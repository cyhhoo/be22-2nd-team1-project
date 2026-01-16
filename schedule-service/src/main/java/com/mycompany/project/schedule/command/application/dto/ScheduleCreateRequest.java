package com.mycompany.project.schedule.command.application.dto;

import com.mycompany.project.schedule.command.domain.aggregate.ScheduleType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class ScheduleCreateRequest {

    @NotNull(message = "Schedule date is required")
    @Future(message = "Schedule date must be in the future")
    private LocalDate scheduleDate;

    @NotNull(message = "Schedule type is required")
    private ScheduleType scheduleType; // SEMESTER_START, MIDTERM_EXAM, HOLIDAY, etc.

    @NotBlank(message = "Schedule content is required")
    private String content; // e.g., "Midterm Exam Start"

    @NotNull(message = "Academic year ID is required")
    private Long academicYearId;
    private String targetGrade; // "1", "2", "3", "ALL"
}