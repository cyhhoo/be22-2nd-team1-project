package com.mycompany.project.user.command.application.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StudentDetailRequest {
    private Integer grade; // Grade level
    private String classNo; // Class number
    private Integer studentNo; // Student number (in class)
}
