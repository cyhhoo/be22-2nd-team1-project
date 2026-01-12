package com.mycompany.project.reservation.query.controller;


import com.mycompany.project.common.response.ApiResponse;
import com.mycompany.project.reservation.query.dto.FacilityDTO;
import com.mycompany.project.reservation.query.dto.ReservationDTO;
import com.mycompany.project.reservation.query.service.ReservationQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reservation")
public class ReservationQueryController {

    private final ReservationQueryService reservationQueryService;

    /**
     * [RES-02] 예약 가능 시설 조회
     * 예) GET /reservation/available?reservationDate=2026-01-15&startTime=10:00
     */
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<FacilityDTO>>> availableFacilities(
            @RequestParam LocalDate reservationDate,
            @RequestParam LocalTime startTime
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(reservationQueryService.getAvailableFacilities(reservationDate, startTime))
        );
    }

    /**
     * [RES-05] 나의 예약 조회
     * 예) GET /reservation/my?studentId=1
     * 예) GET /reservation/my?studentId=1&status=WAITING
     */
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<ReservationDTO>>> myReservations(
            @RequestParam Long studentId,
            @RequestParam(required = false) String status
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(reservationQueryService.getMyReservations(studentId, status))
        );
    }

    /**
     * [RES-08] 관리자 예약 현황 조회
     * 예) GET /reservation/admin/status?adminId=1
     * 예) GET /reservation/admin/status?adminId=1&reservationDate=2026-01-15
     * 예) GET /reservation/admin/status?adminId=1&status=WAITING
     */
    @GetMapping("/admin/status")
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
