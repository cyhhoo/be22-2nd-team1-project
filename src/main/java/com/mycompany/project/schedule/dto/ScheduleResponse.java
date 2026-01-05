package com.mycompany.project.schedule.dto;

import com.mycompany.project.schedule.entity.AcademicSchedule;
import com.mycompany.project.schedule.entity.ScheduleType;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ScheduleResponse {
    private Long scheduleId;
    private LocalDate scheduleDate;
    private ScheduleType scheduleType;
    private String content;

    // Entity -> DTO 변환 생성자
    public ScheduleResponse(AcademicSchedule schedule) {
        this.scheduleId = schedule.getScheduleId();
        this.scheduleDate = schedule.getScheduleDate();
        this.scheduleType = schedule.getScheduleType();
        this.content = schedule.getContent();
    }
}