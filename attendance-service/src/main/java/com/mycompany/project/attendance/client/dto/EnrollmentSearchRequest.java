package com.mycompany.project.attendance.client.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class EnrollmentSearchRequest {
    private List<Long> courseIds;
    private String status;
}
