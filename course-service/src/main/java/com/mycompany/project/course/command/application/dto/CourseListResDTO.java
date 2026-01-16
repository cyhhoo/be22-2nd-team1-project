package com.mycompany.project.course.command.application.dto;

import com.mycompany.project.course.command.domain.aggregate.CourseStatus;
import com.mycompany.project.course.command.domain.aggregate.CourseType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
// 媛뺤쥖 紐⑸줉 議고쉶??DTO (Entity ?꾩껜 ?몄텧 諛⑹? 諛??깅뒫 理쒖쟻??
public class CourseListResDTO {
    private Long courseId;
    private String name;
    private CourseType courseType;
    private CourseStatus status;
    private int currentCount;
    private int maxCapacity;
    private String teacherName; // ?ㅼ젣濡쒕뒗 User Service ?먮뒗 Join???듯빐 媛?몄?????(?ш린?쒕뒗 ID ?먮뒗 Placeholder)
}
