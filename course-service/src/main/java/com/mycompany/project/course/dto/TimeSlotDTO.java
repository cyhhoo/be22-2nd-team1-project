package com.mycompany.project.course.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class TimeSlotDTO {

    @NotBlank(message = "요일은 필수 입력 값입니다.")
    private String dayOfWeek; // 요일 (MON, TUE ...)

    @NotNull(message = "교시는 필수 입력 값입니다.")
    @Min(value = 1, message = "교시는 1 이상이어야 합니다.")
    private Integer period; // 교시 (1, 2, 3 ...)

    @NotBlank(message = "강의실은 필수 입력 값입니다.")
    private String classroom; // 강의실 (Room 101 ...)
}
