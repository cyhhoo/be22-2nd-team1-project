package com.mycompany.project.user.command.application.dto;

import com.mycompany.project.common.enums.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class UserRegisterRequest {
    private String email;
    private String password;
    private String name;
    private LocalDate birthDate;
    private Role role; // ADMIN, TEACHER, STUDENT

    // Role-specific detailed information (Optional)
    private StudentDetailRequest studentDetail;
    private TeacherDetailRequest teacherDetail;
    private AdminDetailRequest adminDetail;
}
