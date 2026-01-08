package com.mycompany.project.auth.query.controller;

import com.mycompany.project.auth.query.dto.LoginRequest;
import com.mycompany.project.auth.query.dto.TokenResponse;
import com.mycompany.project.auth.query.service.AuthQueryService; // Refactored dependency
import com.mycompany.project.common.response.ApiResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthQueryController {

    private final AuthQueryService authQueryService;

    public AuthQueryController(AuthQueryService authQueryService) {
        this.authQueryService = authQueryService;
    }

    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(@RequestBody LoginRequest request) {
        return ApiResponse.success(authQueryService.login(request));
    }

    @PostMapping("/reissue")
    public ApiResponse<TokenResponse> reissue(
            @CookieValue(value = "RefreshToken", required = true) String refreshToken) {
        return ApiResponse.success(authQueryService.reissue(refreshToken));
    }
}
