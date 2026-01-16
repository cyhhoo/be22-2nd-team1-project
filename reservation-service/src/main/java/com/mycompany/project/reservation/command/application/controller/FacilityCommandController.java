package com.mycompany.project.reservation.command.application.controller;

import com.mycompany.project.common.response.ApiResponse;
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

@Tag(name = "Facility Management", description = "Facility registration, update, and deletion API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/facilities")
public class FacilityCommandController {

  private final FacilityCommandService facilityCommandService;

  @Operation(summary = "Register facility", description = "Admin registers a new facility.")
  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<FacilityCommandResponse>> create(
      @RequestParam Long adminId,
      @RequestBody @Valid FacilityCreateRequest request) {
    return ResponseEntity.ok(
        ApiResponse.success(facilityCommandService.create(adminId, request)));
  }

  @Operation(summary = "Update facility", description = "Admin updates facility information.")
  @PutMapping("/{facilityId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<FacilityCommandResponse>> update(
      @PathVariable Long facilityId,
      @RequestParam Long adminId,
      @RequestBody @Valid FacilityUpdateRequest request) {
    return ResponseEntity.ok(
        ApiResponse.success(facilityCommandService.update(adminId, facilityId, request)));
  }

  @Operation(summary = "Delete facility", description = "Admin deletes a facility.")
  @DeleteMapping("/{facilityId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<Void>> delete(
      @PathVariable Long facilityId,
      @RequestParam Long adminId) {
    facilityCommandService.delete(adminId, facilityId);
    return ResponseEntity.ok(ApiResponse.success(null));
  }
}