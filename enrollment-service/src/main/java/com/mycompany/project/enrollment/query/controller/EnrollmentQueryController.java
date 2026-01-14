package com.mycompany.project.enrollment.query.controller;

import com.mycompany.project.common.response.ApiResponse;
import com.mycompany.project.enrollment.query.dto.EnrollmentHistoryResponse;
import com.mycompany.project.enrollment.query.dto.TimetableResponse;
import com.mycompany.project.enrollment.query.service.EnrollmentQueryService;
import com.mycompany.project.exception.BusinessException;
import com.mycompany.project.exception.ErrorCode;
import com.mycompany.project.user.command.domain.aggregate.User;
import com.mycompany.project.user.command.domain.repository.UserRepository;
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

@Tag(name = "수강 신청 (Enrollment)", description = "수강 신청 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/enrollments")
public class EnrollmentQueryController {

  private final EnrollmentQueryService enrollmentQueryService;
  private final UserRepository userRepository;

  // 1. 내 수강 내역 조회
  @Operation(summary = "내 수강 내역 목록 조회", description = "내 수강 신청 목록을 조회합니다.")
  @GetMapping("/history")
  @PreAuthorize("hasRole('STUDENT')")
  public ResponseEntity<ApiResponse<List<EnrollmentHistoryResponse>>> getMyHistory() {
    Long userId = getCurrentUserId();
    List<EnrollmentHistoryResponse> history = enrollmentQueryService.getMyHistory(userId);
    return ResponseEntity.ok(ApiResponse.success(history));
  }

  // 2. 내 시간표 조회
  @Operation(summary = "내 시간표 조회", description = "내 시간표를 조회합니다.")
  @GetMapping("/timetable")
  @PreAuthorize("hasRole('STUDENT')")
  public ResponseEntity<ApiResponse<List<TimetableResponse>>> getMyTimetable() {
    Long userId = getCurrentUserId();
    List<TimetableResponse> timetable = enrollmentQueryService.getMyTimetable(userId);
    return ResponseEntity.ok(ApiResponse.success(timetable));
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
