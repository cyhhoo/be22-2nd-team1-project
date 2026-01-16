package com.mycompany.project.auth.command.controller;

import com.mycompany.project.auth.command.dto.AccountActivationRequest;
import com.mycompany.project.auth.command.service.AuthCommandService;
import com.mycompany.project.auth.query.dto.TokenResponse;
import com.mycompany.project.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth Command", description = "Account activation and authentication state change API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthCommandController {

    private final AuthCommandService authCommandService;

    @Operation(summary = "Activate account", description = "Set password on first login and activate the account.")
    @PostMapping("/activate")
    public ResponseEntity<ApiResponse<TokenResponse>> activate(@RequestBody AccountActivationRequest request) {
        TokenResponse tokens = authCommandService.activateAccount(request);
        return ResponseEntity.ok(ApiResponse.success(tokens));
    }
}
