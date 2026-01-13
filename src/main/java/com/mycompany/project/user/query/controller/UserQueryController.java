package com.mycompany.project.user.query.controller;

import com.mycompany.project.common.response.ApiResponse;
import com.mycompany.project.common.response.PageResponse;
import com.mycompany.project.user.query.dto.UserResponse;
import com.mycompany.project.user.query.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserQueryController {

  private final UserQueryService userQueryService;

  @GetMapping("/me")
  public ResponseEntity<ApiResponse<UserResponse>> getMyInfo() {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    UserResponse response = userQueryService.getMyInfo(email);

    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getUserList(
      @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

    Page<UserResponse> page = userQueryService.getUserList(pageable);

    PageResponse<UserResponse> response = PageResponse.of(page);

    return ResponseEntity.ok(ApiResponse.success(response));
  }

}
