package com.mycompany.project.course.command.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class TimeSlotDTO {

    @NotBlank(message = "?붿씪? ?꾩닔 ?낅젰 媛믪엯?덈떎.")
    private String dayOfWeek; // ?붿씪 (MON, TUE ...)

    @NotNull(message = "援먯떆???꾩닔 ?낅젰 媛믪엯?덈떎.")
    @Min(value = 1, message = "援먯떆??1 ?댁긽?댁뼱???⑸땲??")
    private Integer period; // 援먯떆 (1, 2, 3 ...)

    @NotBlank(message = "媛뺤쓽?ㅼ? ?꾩닔 ?낅젰 媛믪엯?덈떎.")
    private String classroom; // 媛뺤쓽??(Room 101 ...)
}
