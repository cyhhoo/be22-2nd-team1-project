package com.mycompany.project.attendance.query.application.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class AttendanceListResponse {
    private List<AttendanceItem> items;

    @Getter
    @Builder
    public static class AttendanceItem {
        private Long attendanceId;
        private Long studentId;
        private String studentName;
        private LocalDate attendanceDate;
        private Integer period;
        private String attendanceCodeName;
        private String remarks;
    }
}
