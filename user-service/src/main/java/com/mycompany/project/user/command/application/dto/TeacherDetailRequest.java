package com.mycompany.project.user.command.application.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TeacherDetailRequest {
    private Long subjectId; // Reference to tbl_subject.subject_id
    private String subject; // Subject name (e.g., "MATH")
    private Integer homeroomGrade; // Homeroom grade level
    private Integer homeroomClass; // Homeroom class number
}
