package com.mycompany.project.auth.command.controller;

import com.mycompany.project.auth.command.dto.AccountActivationRequest;
import com.mycompany.project.auth.command.service.AuthCommandService;
import com.mycompany.project.auth.query.dto.TokenResponse;
import com.mycompany.project.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
<<<<<<< HEAD:src/main/java/com/mycompany/project/auth/command/controller/AuthCommandController.java

import org.springframework.security.access.prepost.PreAuthorize;

=======
>>>>>>> 3a1188fad3717f0a7412f86adc8c318b581d7226:auth-service/src/main/java/com/mycompany/project/auth/command/controller/AuthCommandController.java
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthCommandController {

    private final AuthCommandService authCommandService;

    @PostMapping("/activate")
    public ResponseEntity<ApiResponse<TokenResponse>> activate(@RequestBody AccountActivationRequest request) {
        TokenResponse tokens = authCommandService.activateAccount(request);
        return ResponseEntity.ok(ApiResponse.success(tokens));
    }
}
