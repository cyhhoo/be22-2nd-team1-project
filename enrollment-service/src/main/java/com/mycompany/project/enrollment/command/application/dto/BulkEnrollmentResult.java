package com.mycompany.project.enrollment.command.application.dto;

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

    private String message; // "?좎껌 ?깃났" or "?뺤썝 珥덇낵", "?쒓컙 以묐났" ??

}