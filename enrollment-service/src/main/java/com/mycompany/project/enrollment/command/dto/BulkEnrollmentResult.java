package com.mycompany.project.enrollment.command.dto;

import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BulkEnrollmentResult {

    private Long courseId;
    private String courseName;

    private boolean isSuccess;

    private String message; // "신청 성공" or "정원 초과", "시간 중복" 등

}