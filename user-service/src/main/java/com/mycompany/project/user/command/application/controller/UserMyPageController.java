package com.mycompany.project.user.command.application.controller;

import com.mycompany.project.common.response.ApiResponse;
import com.mycompany.project.security.CustomUserDetails;
import com.mycompany.project.user.command.application.dto.ChangePasswordRequest;
import com.mycompany.project.user.command.application.service.UserMyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "마이페이지 (MyPage)", description = "사용자 마이페이지 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users/mypage")
public class UserMyPageController {

  private final UserMyPageService userMyPageService;

  @Operation(summary = "비밀번호 변경", description = "현재 비밀번호를 확인 후 새 비밀번호로 변경합니다.")
  @PostMapping("/password")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResponse<Void>> changePassword(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestBody ChangePasswordRequest request) {

    String email = userDetails.getEmail();

    userMyPageService.changePassword(email, request);

    return ResponseEntity.ok(ApiResponse.success(null));
  }

}
