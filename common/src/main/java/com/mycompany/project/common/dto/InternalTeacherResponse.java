package com.mycompany.project.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InternalTeacherResponse {
    private Long id;
    private String name;
    private String email;
    private Integer homeroomGrade;
    private Integer homeroomClassNo;
}
