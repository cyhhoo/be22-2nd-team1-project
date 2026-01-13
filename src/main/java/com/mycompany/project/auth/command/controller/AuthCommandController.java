package com.mycompany.project.auth.command.controller;

import com.mycompany.project.auth.command.dto.AccountActivationRequest;
import com.mycompany.project.auth.command.dto.UserRegisterRequest;
import com.mycompany.project.auth.command.service.AuthCommandService; // Refactored dependency
import com.mycompany.project.auth.query.dto.TokenResponse;
import com.mycompany.project.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthCommandController {

    private final AuthCommandService authCommandService;

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Long>> register(@RequestBody UserRegisterRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authCommandService.registerUser(request)));
    }

    @PostMapping("/activate")
    public ResponseEntity<ApiResponse<TokenResponse>> activate(@RequestBody AccountActivationRequest request) {
        TokenResponse tokens = authCommandService.activateAccount(request);
        return ResponseEntity.ok(ApiResponse.success(tokens));
    }
}
