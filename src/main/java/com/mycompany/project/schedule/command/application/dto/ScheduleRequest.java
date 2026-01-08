package com.mycompany.project.schedule.command.application.dto;

import com.mycompany.project.schedule.command.domain.aggregate.ScheduleType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class ScheduleRequest {
    private LocalDate scheduleDate;
    private ScheduleType scheduleType; // START, EXAM, HOLIDAY
    private String content; // 예: "중간고사 시작"
    private Long academicYearId; // 어떤 학기에 속하는지
}