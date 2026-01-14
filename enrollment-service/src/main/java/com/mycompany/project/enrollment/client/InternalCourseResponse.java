package com.mycompany.project.enrollment.client;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InternalCourseResponse {
    private Long courseId;
    private Long teacherDetailId;
    private Long academicYearId;
    private Long subjectId;
    private String name;
    private String courseType; // Enum -> String
    private String status; // Enum -> String
}
