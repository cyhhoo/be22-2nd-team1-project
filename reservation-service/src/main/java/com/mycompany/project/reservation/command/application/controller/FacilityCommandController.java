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

@RestController
@RequiredArgsConstructor
@RequestMapping("/facility")
public class FacilityCommandController {

  private final FacilityCommandService facilityCommandService;

  /* 시설 등록 */
  @PostMapping
  public ResponseEntity<ApiResponse<FacilityCommandResponse>> create(
      @RequestParam Long adminId,
      @RequestBody @Valid FacilityCreateRequest request
  ) {
    return ResponseEntity.ok(
        ApiResponse.success(facilityCommandService.create(adminId, request))
    );
  }

  /* 시설 수정 */
  @PutMapping("/{facilityId}")
  public ResponseEntity<ApiResponse<FacilityCommandResponse>> update(
      @PathVariable Long facilityId,
      @RequestParam Long adminId,
      @RequestBody @Valid FacilityUpdateRequest request
  ) {
    return ResponseEntity.ok(
        ApiResponse.success(facilityCommandService.update(adminId, facilityId, request))
    );
  }

  /* 시설 삭제 */
  @DeleteMapping("/{facilityId}")
  public ResponseEntity<ApiResponse<Void>> delete(
      @PathVariable Long facilityId,
      @RequestParam Long adminId
  ) {
    facilityCommandService.delete(adminId, facilityId);
    return ResponseEntity.ok(ApiResponse.success(null));
  }
}