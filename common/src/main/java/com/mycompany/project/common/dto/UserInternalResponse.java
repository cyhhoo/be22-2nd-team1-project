package com.mycompany.project.common.dto;

import com.mycompany.project.common.enums.Role;
import com.mycompany.project.common.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInternalResponse {
    private Long userId;
    private String email;
    private String name;
    private Role role;
    private UserStatus status;
    private LocalDate birthDate;
    private String authCode;
}
