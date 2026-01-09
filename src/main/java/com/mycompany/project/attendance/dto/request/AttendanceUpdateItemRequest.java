package com.mycompany.project.attendance.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceUpdateItemRequest {
    private Long enrollmentId;
    private Long attendanceCodeId;
    private String reason;
}
