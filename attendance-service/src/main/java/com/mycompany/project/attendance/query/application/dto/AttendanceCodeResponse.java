package com.mycompany.project.attendance.query.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceCodeResponse {
    private Long attendanceCodeId;
    private String code;
    private String name;
    private Boolean excused;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
