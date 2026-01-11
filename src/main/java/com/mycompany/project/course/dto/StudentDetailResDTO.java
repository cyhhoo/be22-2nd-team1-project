package com.mycompany.project.course.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
// 선생님용 학생 상세 조회 및 메모 관리용 DTO
public class StudentDetailResDTO {
    private Long studentId;
    private String studentName; // 실제로는 User Service에서 가져와야 하나, 여기서는 ID로 대체하거나 Mock함
    private String memo;

    // 출결 현황
    private long attendancePresent;
    private long attendanceLate;
    private long attendanceAbsent;

    // 과제 현황 (Placeholder)
    private long assignmentTotal;
    private long assignmentSubmitted;
}
