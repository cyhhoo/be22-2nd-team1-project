package com.mycompany.project.course.dto;

import com.mycompany.project.course.entity.CourseStatus;
import com.mycompany.project.course.entity.CourseType;
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
    private Integer targetGrade; // [추가]
    private List<TimeSlotResponse> timeSlots; // [추가]

    @Getter
    @Setter
    @NoArgsConstructor
    public static class TimeSlotResponse {
        private String dayOfWeek;
        private Integer period;
    }
}
