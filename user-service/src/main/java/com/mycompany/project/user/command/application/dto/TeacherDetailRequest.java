package com.mycompany.project.user.command.application.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TeacherDetailRequest {
    private Long subjectId; // 담당 과목 ID (tbl_subject.subject_id 참조)
    private String subject; // 담당 과목명 (예: "MATH")
    private Integer homeroomGrade; // 담임 학년
    private Integer homeroomClass; // 담임 반
}
