package com.mycompany.project.attendance.client.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InternalTeacherResponse {
    private Long id;
    private String name;
    private String email;
    private Integer homeroomGrade;
    private Integer homeroomClassNo;
}
