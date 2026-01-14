package com.mycompany.project.auth.command.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class AccountActivationRequest {
    private String email;
    private String name;
    private LocalDate birthDate;
    private String authCode; // 초기 인증코드 (생년월일 6자리 등)
    private String newPassword;

}
