package com.mycompany.project.attendance.client.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InternalEnrollmentResponse {
    private Long enrollmentId;
    private Long studentDetailId;
    private Long courseId;
    private String status;
}
