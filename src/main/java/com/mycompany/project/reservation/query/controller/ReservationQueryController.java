package com.mycompany.project.reservation.query.controller;


import com.mycompany.project.common.response.ApiResponse;
import com.mycompany.project.reservation.query.dto.FacilityDTO;
import com.mycompany.project.reservation.query.dto.ReservationDTO;
import com.mycompany.project.reservation.query.service.ReservationQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
@Tag(name = "예약 가능 시설 조회")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reservation")
public class ReservationQueryController {

    private final ReservationQueryService reservationQueryService;

    /**
     * 예약 가능 시설 조회
     */
    @Operation(summary = "예약 가능 시설 조회")
    @GetMapping("available")
    public ResponseEntity<ApiResponse<List<FacilityDTO>>> availableFacilities(
            @RequestParam LocalDate reservationDate,
            @RequestParam LocalTime startTime
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(reservationQueryService.getAvailableFacilities(reservationDate, startTime))
        );
    }

    /**
     * 나의 예약 조회
     */
    @Operation(summary = "나의 예약 조회")
    @GetMapping("my")
    public ResponseEntity<ApiResponse<List<ReservationDTO>>> myReservations(
            @RequestParam Long studentId,
            @RequestParam(required = false) String status
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(reservationQueryService.getMyReservations(studentId, status))
        );
    }

    /**
     관리자 예약 현황 조회
     */
    @Operation(summary = "관리자 예약 현황 조회")
    @GetMapping("admin/status")
    public ResponseEntity<ApiResponse<List<ReservationDTO>>> adminStatus(
            @RequestParam Long adminId,
            @RequestParam(required = false) LocalDate reservationDate,
            @RequestParam(required = false) String status
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(reservationQueryService.getAdminReservationStatus(adminId, reservationDate, status))
        );
    }

}
