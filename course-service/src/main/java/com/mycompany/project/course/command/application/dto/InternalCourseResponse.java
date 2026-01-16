package com.mycompany.project.course.command.application.dto;

import com.mycompany.project.course.command.domain.aggregate.CourseStatus;
import com.mycompany.project.course.command.domain.aggregate.CourseType;
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
    private CourseType courseType;
    private CourseStatus status;
    private Integer targetGrade; // [異붽?]
    private List<TimeSlotResponse> timeSlots; // [異붽?]

    @Getter
    @Setter
    @NoArgsConstructor
    public static class TimeSlotResponse {
        private String dayOfWeek;
        private Integer period;
    }
}
