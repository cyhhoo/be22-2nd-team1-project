package com.mycompany.project.auth.command.dto;

import com.mycompany.project.user.entity.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserRegisterRequest {
    private String email;
    private String password;
    private String name;
    private String birthDate; // yyyy-MM-dd
    private Role role; // ADMIN, TEACHER, STUDENT
}
