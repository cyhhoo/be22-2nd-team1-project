package com.mycompany.project.auth.command.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AccountActivationRequest {
    private String email;
    private String name;
    private String birthDate; // yyyy-MM-dd
    private String authCode; // 초기 인증코드 (생년월일 6자리 등)
    private String newPassword;
}
