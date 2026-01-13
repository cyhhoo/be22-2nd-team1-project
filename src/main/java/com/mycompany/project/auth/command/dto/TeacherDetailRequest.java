package com.mycompany.project.auth.command.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TeacherDetailRequest {
    private String subject; // 담당 과목명
    private Integer homeroomGrade; // 담임 학년
    private Integer homeroomClass; // 담임 반
}
