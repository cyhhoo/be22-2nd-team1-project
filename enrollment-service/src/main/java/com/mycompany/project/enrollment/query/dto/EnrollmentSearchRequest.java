package com.mycompany.project.enrollment.query.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class EnrollmentSearchRequest {
    private List<Long> courseIds;
    private List<Long> studentIds;
    private String status;
}
