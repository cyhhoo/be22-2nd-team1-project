package com.mycompany.project.user.controller;

import com.mycompany.project.common.dto.ApiDTO;
import com.mycompany.project.user.dto.LoginRequest;
import com.mycompany.project.user.dto.SignupRequest;
import com.mycompany.project.user.dto.TokenResponse;
import com.mycompany.project.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    // 회원가입 API
    @PostMapping("/signup")
    public ApiDTO<Long> signup(@RequestBody SignupRequest request) {
        Long userId = userService.signup(request);
        return ApiDTO.success(userId);
    }

    // 로그인 API
    @PostMapping("/login")
    public ApiDTO<TokenResponse> login(@RequestBody LoginRequest request) {
        TokenResponse tokens = userService.login(request);
        return ApiDTO.success(tokens);
    }
}