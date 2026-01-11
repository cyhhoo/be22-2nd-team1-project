package com.mycompany.project.enrollment.command.controller;

import com.mycompany.project.enrollment.command.dto.BulkEnrollmentResult;
import com.mycompany.project.enrollment.command.dto.EnrollmentApplyRequest;
import com.mycompany.project.enrollment.command.service.EnrollmentCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "수강 신청 (Enrollment)", description = "수강 신청 및 취소 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/enrollments")
public class EnrollmentCommandController {

  private final EnrollmentCommandService enrollmentCommandService;

  // 1. 수강 신청
  @Operation(summary = "강좌 수강 신청", description = "학생이 특정 강좌를 수강 신청합니다.")
  @PostMapping
  public ResponseEntity<Long> register(
      @RequestHeader("X-USER-ID") Long userId,
      @Valid @RequestBody EnrollmentApplyRequest request
  ) {
    Long enrollmentId = enrollmentCommandService.register(userId, request);
    return ResponseEntity.status(201).body(enrollmentId);
  }

  // 2. 수강 취소
  @Operation(summary = "수강 신청 취소", description = "학생이 신청한 강좌를 취소합니다.")
  @DeleteMapping("/{enrollmentId}")
  public ResponseEntity<Void> cancel(
      @RequestHeader("X-USER-ID") Long userId,
      @PathVariable("enrollmentId") Long enrollmentId
  ) {
    enrollmentCommandService.cancel(userId, enrollmentId);
    return ResponseEntity.noContent().build();
  }

  // 3. 일괄 신청
  @Operation(summary = "장바구니 일괄 수강 신청", description = "내 장바구니에 있는 모든 과목을 신청합니다.")
  @PostMapping("/bulk")
  public ResponseEntity<List<BulkEnrollmentResult>> bulkRegister(
      @RequestHeader("X-USER-ID") Long userId
  ) {
    // Service 호출 시 userId만 넘김
    List<BulkEnrollmentResult> results = enrollmentCommandService.bulkRegisterFromCart(userId);
    return ResponseEntity.ok(results);
  }
}
