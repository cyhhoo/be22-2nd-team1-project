package com.mycompany.project.enrollment.query.controller;

import com.mycompany.project.common.response.ApiResponse;
import com.mycompany.project.enrollment.query.dto.CartListResponse;
import com.mycompany.project.enrollment.query.service.CartQueryService;
import com.mycompany.project.exception.BusinessException;
import com.mycompany.project.exception.ErrorCode;
import com.mycompany.project.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "?λ컮援щ땲 (Cart)", description = "?λ컮援щ땲 議고쉶 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/carts")
public class CartQueryController {

  private final CartQueryService cartQueryService;

  @Operation(summary = "???λ컮援щ땲 紐⑸줉 議고쉶", description = "?닿? ?댁? ?섍컯 ?좎껌 ?λ컮援щ땲 紐⑸줉??議고쉶?⑸땲??")
  @GetMapping
  @PreAuthorize("hasRole('STUDENT')")
  public ResponseEntity<ApiResponse<List<CartListResponse>>> getMyCartList() {
    Long userId = getCurrentUserId();
    return ResponseEntity.ok(ApiResponse.success(cartQueryService.getMyCartList(userId)));
  }

  private Long getCurrentUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
      throw new BusinessException(ErrorCode.LOGIN_REQUIRED);
    }

    return ((CustomUserDetails) authentication.getPrincipal()).getUserId();
  }
}
