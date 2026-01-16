package com.mycompany.project.common.dto;

import com.mycompany.project.common.enums.Role;
import com.mycompany.project.common.enums.UserStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserAuthResponse {
    private Long userId;
    private String email;
    private String password;
    private Role role;
    private UserStatus status;
    private boolean isLocked;
}
