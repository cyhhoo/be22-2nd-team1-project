package com.mycompany.project.course.command.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
// ?좎깮?섏슜 ?숈깮 ?곸꽭 議고쉶 諛?硫붾え 愿由ъ슜 DTO
public class StudentDetailResDTO {
    private Long studentId;
    private String studentName; // ?ㅼ젣濡쒕뒗 User Service?먯꽌 媛?몄????섎굹, ?ш린?쒕뒗 ID濡??泥댄븯嫄곕굹 Mock??
    private String memo;

    // 異쒓껐 ?꾪솴
    private long attendancePresent;
    private long attendanceLate;
    private long attendanceAbsent;

    // 怨쇱젣 ?꾪솴 (Placeholder)
    private long assignmentTotal;
    private long assignmentSubmitted;
}
