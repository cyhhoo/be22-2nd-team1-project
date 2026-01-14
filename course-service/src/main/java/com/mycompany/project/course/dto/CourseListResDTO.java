package com.mycompany.project.course.dto;

import com.mycompany.project.course.entity.CourseStatus;
import com.mycompany.project.course.entity.CourseType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
// 강좌 목록 조회용 DTO (Entity 전체 노출 방지 및 성능 최적화)
public class CourseListResDTO {
    private Long courseId;
    private String name;
    private CourseType courseType;
    private CourseStatus status;
    private int currentCount;
    private int maxCapacity;
    private String teacherName; // 실제로는 User Service 또는 Join을 통해 가져와야 함 (여기서는 ID 또는 Placeholder)
}
