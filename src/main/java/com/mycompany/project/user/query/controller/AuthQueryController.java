package com.mycompany.project.user.query.controller;

import com.mycompany.project.common.dto.ApiDTO;
import com.mycompany.project.user.query.dto.LoginRequest;
import com.mycompany.project.user.query.dto.TokenResponse;
import com.mycompany.project.user.query.service.UserQueryService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth") // 같은 URL Prefix 사용 가능 (Spring이 메서드별로 매핑함)

public class AuthQueryController {

    private final UserQueryService userQueryService;

  public AuthQueryController(UserQueryService userQueryService) {
    this.userQueryService = userQueryService;
  }

  @PostMapping("/login")
    public ApiDTO<TokenResponse> login(@RequestBody LoginRequest request) {
        return ApiDTO.success(userQueryService.login(request));
    }
}