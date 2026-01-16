package com.mycompany.project.user.query.controller;

import com.mycompany.project.common.response.ApiResponse;
import com.mycompany.project.common.response.PageResponse;
import com.mycompany.project.security.CustomUserDetails;
import com.mycompany.project.user.query.dto.InternalStudentResponse;
import com.mycompany.project.user.query.dto.InternalTeacherResponse;
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
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "User Query", description = "User information query API")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserQueryController {

  private final UserQueryService userQueryService;

  @Operation(summary = "Get current user info", description = "Retrieve information of the currently logged-in user.")
  @GetMapping("/me")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResponse<UserResponse>> getMyInfo(
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    String email = userDetails.getEmail();

    UserResponse response = userQueryService.getMyInfo(email);

    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @Operation(summary = "Get all users (Admin)", description = "Admin retrieves all users with pagination.")
  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getUserList(
      @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

    Page<UserResponse> page = userQueryService.getUserList(pageable);

    PageResponse<UserResponse> response = PageResponse.of(page);

    return ResponseEntity.ok(ApiResponse.success(response));
  }

  // Internal API for other services (Feign)
  @Hidden
  @GetMapping("/internal/email/{email}")
  public ResponseEntity<UserResponse> getByEmail(@PathVariable String email) {
    UserResponse response = userQueryService.getMyInfo(email);
    return ResponseEntity.ok(response);
  }

  @Hidden
  @GetMapping("/internal/{userId}")
  public ResponseEntity<UserResponse> getById(@PathVariable Long userId) {
    return ResponseEntity.ok(userQueryService.getInternalUser(userId));
  }

  @Hidden
  @GetMapping("/internal/teacher/{userId}")
  public ResponseEntity<InternalTeacherResponse> getTeacherInfo(@PathVariable Long userId) {
    return ResponseEntity.ok(userQueryService.getTeacherInfo(userId));
  }

  @Hidden
  @GetMapping("/internal/student/{userId}")
  public ResponseEntity<InternalStudentResponse> getStudentInfo(@PathVariable Long userId) {
    return ResponseEntity.ok(userQueryService.getStudentInfo(userId));
  }

  @Hidden
  @GetMapping("/internal/students/count-matched")
  public ResponseEntity<Long> countMatchedStudents(
      @RequestParam List<Long> studentIds,
      @RequestParam Integer grade,
      @RequestParam String classNo) {
    return ResponseEntity.ok(userQueryService.countMatchedStudents(studentIds, grade, classNo));
  }

  @Hidden
  @PostMapping("/internal/students/search")
  public ResponseEntity<List<InternalStudentResponse>> searchStudents(
      @RequestBody com.mycompany.project.user.query.dto.StudentSearchRequest request) {
    return ResponseEntity.ok(userQueryService.searchStudents(request));
  }
}
