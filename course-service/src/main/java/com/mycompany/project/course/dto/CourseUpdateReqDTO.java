package com.mycompany.project.course.dto;

import com.mycompany.project.course.entity.CourseStatus;
import com.mycompany.project.course.entity.CourseType;
import lombok.Data;

@Data
public class CourseUpdateReqDTO {

    // 필수 정보가 아닐 수도 있지만, 수정 시 필요한 식별자나 데이터들을 포함합니다.
    // 여기서는 Service에서 ID를 별도로 받으므로 DTO에는 데이터 필드만 구성합니다.

    private String name; // 강좌명
    private CourseType courseType; // 강좌 타입 (MANDATORY, ELECTIVE 등)
    private Integer maxCapacity; // 최대 수강 인원
    private Integer tuition; // 수강료
    private Long subjectId; // 과목 ID
    private Long academicYearId; // 학년 ID
    private Long teacherDetailId; // 교사 상세 ID (담당 교사 변경 시)
    private CourseStatus status; // 강좌 상태 (OPEN, CLOSED 등)
}
