package com.mycompany.project.user.query.controller;

import com.mycompany.project.common.response.ApiResponse;
import com.mycompany.project.common.response.PageResponse;
import com.mycompany.project.jwtsecurity.CustomUserDetails;
import com.mycompany.project.user.query.dto.UserResponse;
import com.mycompany.project.user.query.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserQueryController {

  private final UserQueryService userQueryService;

  @GetMapping("/me")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResponse<UserResponse>> getMyInfo(
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    String email = userDetails.getEmail();

    UserResponse response = userQueryService.getMyInfo(email);

    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getUserList(
      @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

    Page<UserResponse> page = userQueryService.getUserList(pageable);

    PageResponse<UserResponse> response = PageResponse.of(page);

    return ResponseEntity.ok(ApiResponse.success(response));
  }

}
