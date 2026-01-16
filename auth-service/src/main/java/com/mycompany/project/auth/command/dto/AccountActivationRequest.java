package com.mycompany.project.auth.command.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountActivationRequest {
    private String email;
    private String name;
    private LocalDate birthDate;
    private String authCode; // 珥덇린 ?몄쬆肄붾뱶 (?앸뀈?붿씪 6?먮━ ??
    private String newPassword;

}
