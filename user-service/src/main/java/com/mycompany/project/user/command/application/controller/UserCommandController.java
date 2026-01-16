package com.mycompany.project.user.command.application.controller;

import com.mycompany.project.common.response.ApiResponse;
import com.mycompany.project.user.command.application.dto.UserRegisterRequest;
import com.mycompany.project.user.command.application.dto.UserInternalActivateRequest;
import com.mycompany.project.user.command.application.service.UserCommandService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "User Management (Command)", description = "User registration, update, delete, and status change API")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserCommandController {

  private final UserCommandService userCommandService;

  @Operation(summary = "Individual user registration", description = "Register a single user. StudentDetail, TeacherDetail, or AdminDetail is automatically created based on the role.")
  @PostMapping("/register")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<Long>> register(@RequestBody UserRegisterRequest request) {
    Long userId = userCommandService.registerUser(request);
    return ResponseEntity.ok(ApiResponse.success(userId));
  }

  @Operation(summary = "Batch user registration (CSV)", description = "Bulk register users by uploading a CSV file. Required headers: email, password, name, role, birthDate")
  @PostMapping(value = "/batch/import", consumes = "multipart/form-data")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<String>> importUsersBatch(@RequestPart("file") MultipartFile file) {
    int count = userCommandService.importUser(file);
    return ResponseEntity.ok(ApiResponse.success(count + " users registered successfully"));
  }

  @Operation(summary = "Batch user update (CSV)", description = "Bulk update existing users based on email. Required header: email. Optional headers: name, role, birthDate")
  @PutMapping(value = "/batch/update", consumes = "multipart/form-data")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<String>> updateUsersBatch(@RequestPart("file") MultipartFile file) {
    int count = userCommandService.updateUsersInBatch(file);
    return ResponseEntity.ok(ApiResponse.success(count + " users updated successfully"));
  }

  @Hidden
  @PostMapping("/internal/activate")
  public ResponseEntity<Void> internalActivate(@RequestBody UserInternalActivateRequest request) {
    userCommandService.internalActivate(request.getEmail(), request.getEncryptedPassword());
    return ResponseEntity.ok().build();
  }
}
