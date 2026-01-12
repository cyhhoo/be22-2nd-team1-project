package com.mycompany.project.auth.command.controller;

import com.mycompany.project.auth.command.dto.AccountActivationRequest;
import com.mycompany.project.auth.command.dto.UserRegisterRequest;
import com.mycompany.project.auth.command.service.AuthCommandService; // Refactored dependency
import com.mycompany.project.auth.query.dto.TokenResponse;
import com.mycompany.project.common.response.ApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthCommandController {

    private final AuthCommandService authCommandService;

    public AuthCommandController(AuthCommandService authCommandService) {
        this.authCommandService = authCommandService;
    }

    @PostMapping("/register")
    public ApiResponse<Long> register(@RequestBody UserRegisterRequest request) {
        return ApiResponse.success(authCommandService.registerUser(request));
    }

    @PostMapping("/activate")
    public ApiResponse<TokenResponse> activate(@RequestBody AccountActivationRequest request) {
        TokenResponse tokens = authCommandService.activateAccount(request);
        return ApiResponse.success(tokens);
    }
}
