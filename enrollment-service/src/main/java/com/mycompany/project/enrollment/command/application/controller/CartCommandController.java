package com.mycompany.project.enrollment.command.application.controller;

import com.mycompany.project.common.response.ApiResponse;
import com.mycompany.project.enrollment.command.application.dto.CartAddRequest;
import com.mycompany.project.enrollment.command.application.service.CartCommandService;
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

@Tag(name = "Cart", description = "Enrollment cart (pre-enrollment) related API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/carts")
public class CartCommandController {

  private final CartCommandService cartCommandService;

  @Operation(summary = "Add to cart", description = "Add specific course to student's cart.")
  @PostMapping
  @PreAuthorize("hasRole('STUDENT')")
  public ResponseEntity<ApiResponse<Long>> addCart(
      @Valid @RequestBody CartAddRequest request) {
    Long userId = getCurrentUserId();
    Long cartId = cartCommandService.addCart(userId, request);
    return ResponseEntity.status(201).body(ApiResponse.success(cartId));
  }

  @Operation(summary = "Remove from cart", description = "Remove specific course from student's cart.")
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
