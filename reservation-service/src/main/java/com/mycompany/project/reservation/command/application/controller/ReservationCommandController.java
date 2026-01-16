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

@Tag(name = "Facility Reservation")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reservation")
public class ReservationCommandController {

    private final ReservationCommandService reservationCommandService;

    /* Facility Reservation */
    @Operation(summary = "Create facility reservation", description = "Register a new reservation for a specific facility.")
    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<ReservationCommandResponse>> createReservation(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid ReservationCreateRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success(reservationCommandService.create(userDetails.getUserId(), request)));
    }

    /* Cancel Reservation */
    @Operation(summary = "Cancel facility reservation", description = "Cancel an existing reservation.")
    @DeleteMapping("{reservationId}")

    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<Void>> cancelReservation(
            @PathVariable Long reservationId,
            @RequestParam Long studentId) {
        reservationCommandService.cancel(reservationId, studentId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /* Change Reservation */
    @Operation(summary = "Change facility reservation", description = "Modify the date or time of an existing reservation.")
    @PutMapping("{reservationId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<Void>> changeReservation(
            @PathVariable Long reservationId,
            @RequestParam Long studentId,
            @RequestBody @Valid ReservationChangeRequest request) {
        reservationCommandService.change(reservationId, studentId, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /* Approve/Reject Reservation */
    @Operation(summary = "Approve/Reject reservation", description = "Admin approves or rejects a facility reservation request.")
    @PutMapping("{reservationId}/approve")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> approveReservation(
            @PathVariable Long reservationId,
            @RequestParam Long adminId,
            @RequestBody @Valid ReservationApproveRequest request) {
        reservationCommandService.approve(adminId, reservationId, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
