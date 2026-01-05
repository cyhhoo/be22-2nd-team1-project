package com.mycompany.project.attendance.dto;

import lombok.Data;

@Data
public class AttendanceDTO {
    private Long attendanceId;
    private String studentName;
    private String status;
}
