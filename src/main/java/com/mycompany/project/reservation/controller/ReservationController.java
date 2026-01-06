package com.mycompany.project.reservation.controller;

import com.mycompany.project.reservation.repository.ReservationRepository;
import com.mycompany.project.reservation.service.ReservationService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "시설 예약 (Reservation)", description = "시설/상담 예약 및 승인 관리 API")
@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
    private final ReservationService reservationService;
    private final ReservationRepository reservationRepository;


  public ReservationController(ReservationService reservationService, ReservationRepository reservationRepository) {
    this.reservationService = reservationService;
    this.reservationRepository = reservationRepository;
  }

  /*
     * [구현 가이드: Service 의존성 주입]
     * private final ReservationService reservationService;
     */

    @Operation(summary = "예약 신청", description = "시설 또는 상담 예약을 신청합니다. (시간 중복 체크 필수)")
    @PostMapping
    public void createReservation() {
        /*
         * [구현 가이드: 예약 신청 로직 순서]
         * 1. 예약 요청 정보(대상 날짜, 시작 시간, 종료 시간, 대상 시설/선생님ID, 신청자ID)를 받습니다.
         * 2. 중복 예약 검증 (Time Overlap Check):
         * - 해당 대상(시설/선생님)의 기존 예약 중 상태가 '취소(CANCELLED)'가 아닌 건들을 조회합니다.
         * - 요청한 시간 범위가 기존 예약의 시간 범위와 겹치는지 확인합니다.
         * - 겹치는 조건 예시 (SQL Where 절):
         * (Start < Res_End) AND (End > Res_Start)
         * - 겹치는 건이 1건이라도 있으면 ReservationOverlapException 발생.
         * 3. 검증 통과 시, 예약 상태를 'PENDING(승인 대기)'으로 하여 DB에 저장합니다.
         */
    }

    @Operation(summary = "예약 승인/거절", description = "신청된 예약을 교사가 승인하거나 거절합니다.")
    @PutMapping("/{reservationId}/status")
    public void updateReservationStatus(@PathVariable Long reservationId) {
        /*
         * [구현 가이드: 상태 변경 워크플로우]
         * 1. 1. 예약 ID로 예약 정보를 조회합니다.
         * 2. 권한 체크: 승인/거절 권한이 있는 사용자(교사/관리자)인지 확인합니다.
         * 3. 상태 변경:
         * - 승인 시: Status -> 'APPROVED'
         * - 거절 시: Status -> 'REJECTED'
         * 4. 'APPROVED' 상태가 된 예약은 신청자가 임의로 취소할 수 없도록 제한하는 로직이 취소 API에 필요할 수 있습니다.
         */
    }
}
