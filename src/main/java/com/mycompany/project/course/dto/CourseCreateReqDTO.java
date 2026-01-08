package com.mycompany.project.course.dto;

import com.mycompany.project.course.entity.CourseType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CourseCreateReqDTO {

    private String name; // 강좌명
    private CourseType courseType; // 수업 유형 (MANDATORY, ELECTIVE)
    private Integer maxCapacity; // 최대 정원
    private Integer tuition; // 수강료
    private Long subjectId; // 과목 ID
    private Long academicYearId; // 학년/학기 ID
    private Long teacherDetailId; // 담당 교사 ID

    // 시간표 목록 (요일, 교시, 강의실)
    private List<TimeSlotDTO> timeSlots;

    @Data
    @NoArgsConstructor
    public static class TimeSlotDTO {
        private String dayOfWeek; // 요일 (MON, TUE ...)
        private Integer period; // 교시 (1, 2, 3 ...)
        private String classroom; // 강의실 (Room 101 ...)
    }
}
