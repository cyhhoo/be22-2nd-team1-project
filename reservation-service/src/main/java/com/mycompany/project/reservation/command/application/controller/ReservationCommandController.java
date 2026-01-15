package com.mycompany.project.reservation.command.application.controller;

import com.mycompany.project.common.response.ApiResponse;
import com.mycompany.project.reservation.command.application.dto.request.ReservationApproveRequest;
import com.mycompany.project.reservation.command.application.dto.request.ReservationChangeRequest;
import com.mycompany.project.reservation.command.application.dto.request.ReservationCreateRequest;
import com.mycompany.project.reservation.command.application.dto.response.ReservationCommandResponse;
import com.mycompany.project.reservation.command.application.service.ReservationCommandService;
import com.mycompany.project.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = " 시설 예약 ")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reservation")
public class ReservationCommandController {

    private final ReservationCommandService reservationCommandService;

    private Long mockStudent() { return 1L; }

    /* 시설 예약*/
    @Operation(summary = "시설 예약", description = "ex) reservationId=1, studentId =1")
    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<ReservationCommandResponse>> createReservation(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid ReservationCreateRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(reservationCommandService.create(userDetails.getUserId() , request))
        );
    }


    /* 시설 예약 취소 */
    @Operation(summary = "시설 예약 취소",description = "ex) reservationId = 1, studentId = 1")
    @DeleteMapping("{reservationId}")

    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<Void>> cancelReservation(
            @PathVariable Long reservationId,
            @RequestParam Long studentId
    ) {
        reservationCommandService.cancel(reservationId, studentId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    /* 시설 예약 변경 */
    @Operation(summary = "시설 예약 변경",description = "ex) ")
    @PutMapping("{reservationId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<Void>> changeReservation(
            @PathVariable Long reservationId,
            @RequestParam Long studentId,
            @RequestBody @Valid ReservationChangeRequest request
    ) {
        reservationCommandService.change(reservationId, studentId, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /* 시설 예약 승인, 거부 */
    @Operation(summary = "시설 예약 승인, 거부")
    @PutMapping("{reservationId}/approve")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> approveReservation(
            @PathVariable Long reservationId,
            @RequestParam Long adminId,
            @RequestBody @Valid ReservationApproveRequest request
    ) {
        reservationCommandService.approve(adminId, reservationId, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}

