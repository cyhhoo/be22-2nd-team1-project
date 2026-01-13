package com.mycompany.project.enrollment.command.controller;

import com.mycompany.project.common.response.ApiResponse;
import com.mycompany.project.enrollment.command.dto.BulkEnrollmentResult;
import com.mycompany.project.enrollment.command.dto.EnrollmentApplyRequest;
import com.mycompany.project.enrollment.command.service.EnrollmentCommandService;
import com.mycompany.project.exception.BusinessException;
import com.mycompany.project.exception.ErrorCode;
import com.mycompany.project.user.command.domain.aggregate.User;
import com.mycompany.project.user.command.domain.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "수강 신청 (Enrollment)", description = "수강 신청 및 취소 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/enrollments")
public class EnrollmentCommandController {

  private final EnrollmentCommandService enrollmentCommandService;
  private final UserRepository userRepository;

  // 1. 수강 신청
  @Operation(summary = "강좌 수강 신청", description = "학생이 특정 강좌를 수강 신청합니다.")
  @PostMapping
  public ResponseEntity<ApiResponse<Long>> register(
      @Valid @RequestBody EnrollmentApplyRequest request
  ) {
    Long userId = getCurrentUserId();
    Long enrollmentId = enrollmentCommandService.register(userId, request);
    return ResponseEntity.status(201).body(ApiResponse.success(enrollmentId));

  /*  return ResponseEntity.ok(
        ApiResponse.success(enrollmentCommandService.register(userId, request))
    );*/
  }

  // 2. 수강 취소
  @Operation(summary = "수강 신청 취소", description = "학생이 신청한 강좌를 취소합니다.")
  @DeleteMapping("/{enrollmentId}")
  public ResponseEntity<ApiResponse<Void>> cancel(
      @PathVariable("enrollmentId") Long enrollmentId
  ) {
    Long userId = getCurrentUserId();
    enrollmentCommandService.cancel(userId, enrollmentId);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  // 3. 일괄 신청
  @Operation(summary = "장바구니 일괄 수강 신청", description = "내 장바구니에 있는 모든 과목을 신청합니다.")
  @PostMapping("/bulk")
  public ResponseEntity<ApiResponse<List<BulkEnrollmentResult>>> bulkRegister() {
    Long userId = getCurrentUserId();
    List<BulkEnrollmentResult> results = enrollmentCommandService.bulkRegisterFromCart(userId);
    return ResponseEntity.ok(ApiResponse.success(results));
  }

  private Long getCurrentUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || authentication.getName() == null) {
      throw new BusinessException(ErrorCode.LOGIN_REQUIRED);
    }

    String email = authentication.getName();

    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

    return user.getUserId();
  }
}
