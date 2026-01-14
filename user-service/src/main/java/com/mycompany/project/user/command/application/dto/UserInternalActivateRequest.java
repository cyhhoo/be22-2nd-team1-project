package com.mycompany.project.user.command.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserInternalActivateRequest {
    private String email;
    private String encryptedPassword;
}
