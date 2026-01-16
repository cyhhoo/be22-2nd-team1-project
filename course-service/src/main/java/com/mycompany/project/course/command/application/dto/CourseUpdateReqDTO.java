package com.mycompany.project.course.command.application.dto;

import com.mycompany.project.course.command.domain.aggregate.CourseStatus;
import com.mycompany.project.course.command.domain.aggregate.CourseType;
import lombok.Data;

@Data
public class CourseUpdateReqDTO {

    // ?꾩닔 ?뺣낫媛 ?꾨땺 ?섎룄 ?덉?留? ?섏젙 ???꾩슂???앸퀎?먮굹 ?곗씠?곕뱾???ы븿?⑸땲??
    // ?ш린?쒕뒗 Service?먯꽌 ID瑜?蹂꾨룄濡?諛쏆쑝誘濡?DTO?먮뒗 ?곗씠???꾨뱶留?援ъ꽦?⑸땲??

    private String name; // 媛뺤쥖紐?
    private CourseType courseType; // 媛뺤쥖 ???(MANDATORY, ELECTIVE ??
    private Integer maxCapacity; // 理쒕? ?섍컯 ?몄썝
    private Integer tuition; // ?섍컯猷?
    private Long subjectId; // 怨쇰ぉ ID
    private Long academicYearId; // ?숇뀈 ID
    private Long teacherDetailId; // 援먯궗 ?곸꽭 ID (?대떦 援먯궗 蹂寃???
    private CourseStatus status; // 媛뺤쥖 ?곹깭 (OPEN, CLOSED ??
}
