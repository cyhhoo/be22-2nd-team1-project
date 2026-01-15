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
    private String courseType; // Enum -> String
    private String status; // Enum -> String
    private Integer targetGrade; // [추가] 대상 학년
    private List<TimeSlotResponse> timeSlots; // [추가] 시간표 정보

    @Getter
    @Setter
    @NoArgsConstructor
    public static class TimeSlotResponse {
        private String dayOfWeek;
        private Integer period;
    }
}
