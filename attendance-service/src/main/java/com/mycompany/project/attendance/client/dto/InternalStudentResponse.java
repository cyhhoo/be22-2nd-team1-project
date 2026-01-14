package com.mycompany.project.attendance.client.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InternalStudentResponse {
    private Long id;
    private Integer grade;
    private String classNo;
}
