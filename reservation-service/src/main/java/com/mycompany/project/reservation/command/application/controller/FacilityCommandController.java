package com.mycompany.project.reservation.command.application.controller;

import com.mycompany.project.common.response.ApiResponse;
import com.mycompany.project.reservation.command.application.dto.request.FacilityChangeRequest;
import com.mycompany.project.reservation.command.application.dto.request.FacilityCreateRequest;
import com.mycompany.project.reservation.command.application.dto.request.FacilityUpdateRequest;
import com.mycompany.project.reservation.command.application.dto.response.FacilityCommandResponse;
import com.mycompany.project.reservation.command.application.service.FacilityCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "시설 관리 (Facility)", description = "시설 등록/수정/삭제 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/facilities")
public class FacilityCommandController {

  private final FacilityCommandService facilityCommandService;

  @Operation(summary = "시설 등록", description = "관리자가 새로운 시설을 등록합니다.")
  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<FacilityCommandResponse>> create(
      @RequestParam Long adminId,
      @RequestBody @Valid FacilityCreateRequest request) {
    return ResponseEntity.ok(
        ApiResponse.success(facilityCommandService.create(adminId, request)));
  }

  @Operation(summary = "시설 수정", description = "관리자가 시설 정보를 수정합니다.")
  @PutMapping("/{facilityId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<FacilityCommandResponse>> update(
      @PathVariable Long facilityId,
      @RequestParam Long adminId,
      @RequestBody @Valid FacilityUpdateRequest request) {
    return ResponseEntity.ok(
        ApiResponse.success(facilityCommandService.update(adminId, facilityId, request)));
  }

  @Operation(summary = "시설 삭제", description = "관리자가 시설을 삭제합니다.")
  @DeleteMapping("/{facilityId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<Void>> delete(
      @PathVariable Long facilityId,
      @RequestParam Long adminId) {
    facilityCommandService.delete(adminId, facilityId);
    return ResponseEntity.ok(ApiResponse.success(null));
  }
}