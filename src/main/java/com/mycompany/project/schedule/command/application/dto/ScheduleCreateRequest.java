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

    @NotNull(message = "일정 날짜는 필수입니다.")
    @Future(message = "일정 날짜는 미래여야 합니다.") // 비즈니스 요구사항에 따라
    private LocalDate scheduleDate;

    @NotNull(message = "일정 타입은 필수입니다.")
    private ScheduleType scheduleType; // SEMESTER_START, MIDTERM_EXAM, HOLIDAY 등

    @NotBlank(message = "일정 내용은 필수입니다.")
    private String content; // 예: "중간고사 시작"

    @NotNull(message = "일정 학기은 필수입니다.")
    private Long academicYearId; // 어떤 학기에 속하는지
    private String targetGrade; // "1", "2", "3", "ALL"
}