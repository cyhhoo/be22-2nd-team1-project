package com.mycompany.project.user.query.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InternalTeacherResponse {
    private Long id;
    private Long subjectId;
    private Integer homeroomGrade;
    private Integer homeroomClassNo;
}
