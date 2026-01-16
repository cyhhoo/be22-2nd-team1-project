package com.mycompany.project.reservation.query.controller;

import com.mycompany.project.common.response.ApiResponse;
import com.mycompany.project.reservation.query.dto.FacilityDTO;
import com.mycompany.project.reservation.query.dto.ReservationDTO;
import com.mycompany.project.reservation.query.service.ReservationQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Tag(name = "Facility Reservation Query", description = "Query available facilities and reservation status")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reservation")
public class ReservationQueryController {

        private final ReservationQueryService reservationQueryService;

        /**
         * Search available facilities
         */
        @Operation(summary = "Search available facilities", description = "Search facilities available at a specific date and time.")
        @GetMapping("available")
        @PreAuthorize("hasRole('STUDENT')")
        public ResponseEntity<ApiResponse<List<FacilityDTO>>> availableFacilities(
                        @RequestParam LocalDate reservationDate,
                        @RequestParam LocalTime startTime) {
                return ResponseEntity.ok(
                                ApiResponse.success(reservationQueryService.getAvailableFacilities(reservationDate,
                                                startTime)));
        }

        /**
         * Retrieve student's own reservations
         */
        @Operation(summary = "Get my reservations", description = "Retrieve reservations of the currently logged-in student.")
        @GetMapping("my")
        @PreAuthorize("hasRole('STUDENT')")
        public ResponseEntity<ApiResponse<List<ReservationDTO>>> myReservations(
                        @RequestParam Long studentId,
                        @RequestParam(required = false) String status) {
                return ResponseEntity.ok(
                                ApiResponse.success(reservationQueryService.getMyReservations(studentId, status)));
        }

        /**
         * Admin: Retrieve facility reservation status
         */
        @Operation(summary = "Admin: Get reservation status", description = "Admin retrieves reservation status for their facilities.")
        @GetMapping("admin/status")
        @PreAuthorize("isAuthenticated()")
        public ResponseEntity<ApiResponse<List<ReservationDTO>>> adminStatus(
                        @RequestParam Long adminId,
                        @RequestParam(required = false) LocalDate reservationDate,
                        @RequestParam(required = false) String status) {
                return ResponseEntity.ok(
                                ApiResponse.success(reservationQueryService.getAdminReservationStatus(adminId,
                                                reservationDate, status)));
        }
}
