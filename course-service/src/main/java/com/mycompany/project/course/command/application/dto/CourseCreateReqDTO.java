package com.mycompany.project.course.command.application.dto;

import com.mycompany.project.course.command.domain.aggregate.CourseType;
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

    @NotBlank(message = "媛뺤쥖紐낆? ?꾩닔 ?낅젰 媛믪엯?덈떎.")
    private String name; // 媛뺤쥖紐?

    @NotNull(message = "?섏뾽 ?좏삎? ?꾩닔 ?낅젰 媛믪엯?덈떎.")
    private CourseType courseType; // ?섏뾽 ?좏삎 (MANDATORY, ELECTIVE)

    @NotNull(message = "理쒕? ?뺤썝? ?꾩닔 ?낅젰 媛믪엯?덈떎.")
    @Min(value = 1, message = "理쒕? ?뺤썝? 1紐??댁긽?댁뼱???⑸땲??")
    private Integer maxCapacity; // 理쒕? ?뺤썝

    @NotNull(message = "?섍컯猷뚮뒗 ?꾩닔 ?낅젰 媛믪엯?덈떎.")
    @Min(value = 0, message = "?섍컯猷뚮뒗 0???댁긽?댁뼱???⑸땲??")
    private Integer tuition; // ?섍컯猷?

    @NotNull(message = "怨쇰ぉ ID???꾩닔 ?낅젰 媛믪엯?덈떎.")
    private Long subjectId; // 怨쇰ぉ ID

    @NotNull(message = "?숇뀈/?숆린 ID???꾩닔 ?낅젰 媛믪엯?덈떎.")
    private Long academicYearId; // ?숇뀈/?숆린 ID

    @NotNull(message = "?대떦 援먯궗 ID???꾩닔 ?낅젰 媛믪엯?덈떎.")
    private Long teacherDetailId; // ?대떦 援먯궗 ID

    // ?쒓컙??紐⑸줉 (?붿씪, 援먯떆, 媛뺤쓽??
    @Valid // ?대? DTO(TimeSlotDTO)??Validation???섑뻾
    private List<TimeSlotDTO> timeSlots;
}
