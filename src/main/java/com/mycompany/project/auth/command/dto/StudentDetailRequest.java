package com.mycompany.project.auth.command.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StudentDetailRequest {
    private Integer grade; // 학년
    private String classNo; // 반
    private Integer studentNo; // 번호
}
