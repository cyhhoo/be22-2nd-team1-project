package com.mycompany.project.attendance.command.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceUpdateRequest {
    private Long courseId;
    private LocalDate classDate;
    private Integer period;
    private Long userId;
    private List<AttendanceUpdateItemRequest> items;
}
