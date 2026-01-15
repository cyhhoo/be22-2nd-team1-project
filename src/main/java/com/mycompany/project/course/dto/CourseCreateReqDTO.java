package com.mycompany.project.course.dto;

import com.mycompany.project.course.entity.CourseType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CourseCreateReqDTO {

    @NotBlank(message = "강좌명은 필수 입력 값입니다.")
    private String name; // 강좌명

    @NotNull(message = "수업 유형은 필수 입력 값입니다.")
    private CourseType courseType; // 수업 유형 (MANDATORY, ELECTIVE)

    @NotNull(message = "최대 정원은 필수 입력 값입니다.")
    @Min(value = 1, message = "최대 정원은 1명 이상이어야 합니다.")
    private Integer maxCapacity; // 최대 정원

    @NotNull(message = "수강료는 필수 입력 값입니다.")
    @Min(value = 0, message = "수강료는 0원 이상이어야 합니다.")
    private Integer tuition; // 수강료

    @NotNull(message = "과목 ID는 필수 입력 값입니다.")
    private Long subjectId; // 과목 ID

    @NotNull(message = "학년/학기 ID는 필수 입력 값입니다.")
    private Long academicYearId; // 학년/학기 ID

    @NotNull(message = "담당 교사 ID는 필수 입력 값입니다.")
    private Long teacherDetailId; // 담당 교사 ID

    // 시간표 목록 (요일, 교시, 강의실)
    @Valid // 내부 DTO(TimeSlotDTO)의 Validation도 수행
    private List<TimeSlotDTO> timeSlots;
}
