package com.mycompany.project.enrollment.command.controller;

import com.mycompany.project.common.response.ApiResponse;
import com.mycompany.project.enrollment.command.application.dto.BulkEnrollmentResult;
import com.mycompany.project.enrollment.command.application.dto.EnrollmentApplyRequest;
import com.mycompany.project.enrollment.command.application.service.EnrollmentCommandService;
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

import java.util.List;

@Tag(name = "?섍컯 ?좎껌 (Enrollment)", description = "?섍컯 ?좎껌 諛?痍⑥냼 愿??API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/enrollments")
public class EnrollmentCommandController {

  private final EnrollmentCommandService enrollmentCommandService;

  // 1. ?섍컯 ?좎껌
  @Operation(summary = "媛뺤쥖 ?섍컯 ?좎껌", description = "?숈깮???뱀젙 媛뺤쥖瑜??섍컯 ?좎껌?⑸땲??")
  @PostMapping
  @PreAuthorize("hasRole('STUDENT')")
  public ResponseEntity<ApiResponse<Long>> register(
      @Valid @RequestBody EnrollmentApplyRequest request) {
    Long userId = getCurrentUserId();
    Long enrollmentId = enrollmentCommandService.register(userId, request);
    return ResponseEntity.status(201).body(ApiResponse.success(enrollmentId));
  }

  // 2. ?섍컯 痍⑥냼
  @Operation(summary = "?섍컯 ?좎껌 痍⑥냼", description = "?숈깮???좎껌??媛뺤쥖瑜?痍⑥냼?⑸땲??")
  @DeleteMapping("/{enrollmentId}")
  @PreAuthorize("hasRole('STUDENT')")
  public ResponseEntity<ApiResponse<Void>> cancel(
      @PathVariable("enrollmentId") Long enrollmentId) {
    Long userId = getCurrentUserId();
    enrollmentCommandService.cancel(userId, enrollmentId);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  // 3. ?쇨큵 ?좎껌
  @Operation(summary = "?λ컮援щ땲 ?쇨큵 ?섍컯 ?좎껌", description = "???λ컮援щ땲???덈뒗 紐⑤뱺 怨쇰ぉ???좎껌?⑸땲??")
  @PostMapping("/bulk")
  @PreAuthorize("hasRole('STUDENT')")
  public ResponseEntity<ApiResponse<List<BulkEnrollmentResult>>> bulkRegister() {
    Long userId = getCurrentUserId();
    List<BulkEnrollmentResult> results = enrollmentCommandService.bulkRegisterFromCart(userId);
    return ResponseEntity.ok(ApiResponse.success(results));
  }

  private Long getCurrentUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
      throw new BusinessException(ErrorCode.LOGIN_REQUIRED);
    }

    return ((CustomUserDetails) authentication.getPrincipal()).getUserId();
  }
}
