package com.mycompany.project.enrollment.query.dto;

import com.mycompany.project.common.enums.EnrollmentStatus;
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
    private EnrollmentStatus status;
}
