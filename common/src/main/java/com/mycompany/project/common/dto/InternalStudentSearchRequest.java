package com.mycompany.project.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InternalStudentSearchRequest {
    private List<Long> studentIds;
    private Integer grade;
    private String classNo;
}
