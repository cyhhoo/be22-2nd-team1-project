package com.mycompany.project.enrollment.client;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class InternalCourseResponse {
    private Long courseId;
    private Long teacherDetailId;
    private Long academicYearId;
    private Long subjectId;
    private String name;
    private String courseType; // Enum as String
    private String status; // Enum as String
    private Integer targetGrade; // Target grade level
    private List<TimeSlotResponse> timeSlots; // Time slot information

    @Getter
    @Setter
    @NoArgsConstructor
    public static class TimeSlotResponse {
        private String dayOfWeek;
        private Integer period;
    }
}
