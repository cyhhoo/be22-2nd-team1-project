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

@Tag(name = "인증 관리 (Auth Command)", description = "계정 활성화 등 인증 상태 변경 API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthCommandController {

    private final AuthCommandService authCommandService;

    @Operation(summary = "계정 활성화", description = "최초 로그인 시 비밀번호를 설정하고 계정을 활성화합니다.")
    @PostMapping("/activate")
    public ResponseEntity<ApiResponse<TokenResponse>> activate(@RequestBody AccountActivationRequest request) {
        TokenResponse tokens = authCommandService.activateAccount(request);
        return ResponseEntity.ok(ApiResponse.success(tokens));
    }
}
