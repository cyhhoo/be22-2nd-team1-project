package com.mycompany.project.schedule.query.dto;

import com.mycompany.project.schedule.command.domain.aggregate.ScheduleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor()
@AllArgsConstructor
public class ScheduleDTO {

    private Long scheduleId;
    private LocalDate scheduleDate;
    private ScheduleType scheduleType;
    private String content;
    private String targetGrade;

}