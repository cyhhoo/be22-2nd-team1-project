package com.mycompany.project.course.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
// 교사 주간 시간표 조회용 DTO (요일/교시별 강의 정보)
public class TeacherTimetableResDTO {

    private List<TimeSlotInfo> timeSlots;

    @Getter
    @Builder
    public static class TimeSlotInfo {
        private String dayOfWeek; // 요일 (MON, TUE, ...)
        private Integer period; // 교시 (1, 2, ...)
        private Long courseId; // 강좌 ID (클릭 시 이동용)
        private String courseName; // 강좌명
        private String classroom; // 강의실
        private String courseType; // 이수 구분 (색상 구분용)
    }
}
