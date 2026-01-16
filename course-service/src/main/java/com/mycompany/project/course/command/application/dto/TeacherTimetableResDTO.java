package com.mycompany.project.course.command.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
// 援먯궗 二쇨컙 ?쒓컙??議고쉶??DTO (?붿씪/援먯떆蹂?媛뺤쓽 ?뺣낫)
public class TeacherTimetableResDTO {

    private List<TimeSlotInfo> timeSlots;

    @Getter
    @Builder
    public static class TimeSlotInfo {
        private String dayOfWeek; // ?붿씪 (MON, TUE, ...)
        private Integer period; // 援먯떆 (1, 2, ...)
        private Long courseId; // 媛뺤쥖 ID (?대┃ ???대룞??
        private String courseName; // 媛뺤쥖紐?
        private String classroom; // 媛뺤쓽??
        private String courseType; // ?댁닔 援щ텇 (?됱긽 援щ텇??
    }
}
