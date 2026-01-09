package com.mycompany.project.attendance.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceConfirmRequest {
    private Long courseId;
    private LocalDate classDate;
    private Integer period;
    private Long userId;
}
