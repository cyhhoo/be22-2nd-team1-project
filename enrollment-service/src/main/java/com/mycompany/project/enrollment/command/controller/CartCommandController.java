package com.mycompany.project.enrollment.command.controller;

import com.mycompany.project.common.response.ApiResponse;
import com.mycompany.project.enrollment.command.dto.CartAddRequest;
import com.mycompany.project.enrollment.command.service.CartCommandService;
import com.mycompany.project.exception.BusinessException;
import com.mycompany.project.exception.ErrorCode;
import com.mycompany.project.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "장바구니 (Cart)", description = "수강 신청 장바구니(예비 수강 신청) 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/carts")
public class CartCommandController {

  private final CartCommandService cartCommandService;

  // 1. 장바구니 담기
  @Operation(summary = "장바구니 담기", description = "학생이 특정 강좌를 장바구니에 넣습니다.")
  @PostMapping
  @PreAuthorize("hasRole('STUDENT')")
  public ResponseEntity<ApiResponse<Long>> addCart(
      @Valid @RequestBody CartAddRequest request) {
    Long userId = getCurrentUserId();
    Long cartId = cartCommandService.addCart(userId, request);
    return ResponseEntity.status(201).body(ApiResponse.success(cartId));
  }

  // 2. 장바구니 삭제 (과목 ID 기준)
  @Operation(summary = "장바구니 담기 취소", description = "학생이 장바구니에 넣은 특정 강좌를 삭제합니다.")
  @DeleteMapping("/courses/{courseId}")
  @PreAuthorize("hasRole('STUDENT')")
  public ResponseEntity<ApiResponse<Void>> removeCart(
      @PathVariable("courseId") Long courseId) {
    Long userId = getCurrentUserId();
    cartCommandService.removeCart(userId, courseId);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  private Long getCurrentUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
      throw new BusinessException(ErrorCode.LOGIN_REQUIRED);
    }

    return ((CustomUserDetails) authentication.getPrincipal()).getUserId();
  }
}
