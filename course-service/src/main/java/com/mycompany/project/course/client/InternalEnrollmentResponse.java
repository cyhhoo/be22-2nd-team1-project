package com.mycompany.project.course.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InternalEnrollmentResponse {
    private Long enrollmentId;
    private Long studentDetailId;
    private Long courseId;
    private String status;
    private String memo;
}
