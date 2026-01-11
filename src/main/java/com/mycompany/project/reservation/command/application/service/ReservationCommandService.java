package com.mycompany.project.reservation.command.application.service;

import com.mycompany.project.reservation.command.application.dto.request.ReservationApproveRequest;
import com.mycompany.project.reservation.command.application.dto.request.ReservationChangeRequest;
import com.mycompany.project.reservation.command.application.dto.request.ReservationCreateRequest;
import com.mycompany.project.reservation.command.application.dto.response.ReservationCommandResponse;
import com.mycompany.project.reservation.command.domain.aggregate.Reservation;
import com.mycompany.project.reservation.command.infrastructure.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationCommandService {

    private final ReservationRepository reservationRepository;

    /* 예약 신청 */
    public ReservationCommandResponse create(Long studentId, ReservationCreateRequest req) {

        if (reservationRepository.existsByFacilityIdAndReservationDateAndStartTime(
                req.getFacilityId(), req.getReservationDate(), req.getStartTime())) {
            throw new IllegalStateException("이미 예약된 시간입니다.");
        }

        Reservation reservation =
                Reservation.create(req.getFacilityId(), studentId,
                        req.getReservationDate(), req.getStartTime());

        return ReservationCommandResponse.from(
                reservationRepository.save(reservation)
        );
    }

    /* 예약 취소 */
    public void cancel(Long reservationId, Long studentId) {
        Reservation r = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalStateException("예약 없음"));

        if (!r.getStudentId().equals(studentId)) {
            throw new IllegalStateException("본인 예약만 취소 가능");
        }

        r.cancel();
    }

    /* 예약 변경 */
    public void change(Long reservationId, Long studentId, ReservationChangeRequest req) {
        Reservation r = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalStateException("예약 없음"));

        if (!r.getStudentId().equals(studentId)) {
            throw new IllegalStateException("본인 예약만 변경 가능");
        }

        if (reservationRepository.existsByFacilityIdAndReservationDateAndStartTime(
                r.getFacilityId(), req.getReservationDate(), req.getStartTime())) {
            throw new IllegalStateException("이미 예약된 시간입니다.");
        }

        r.change(req.getReservationDate(), req.getStartTime());
    }

    /* 관리자 승인/거부 */
    public void approve(Long reservationId, Long id, ReservationApproveRequest req) {
        Reservation r = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalStateException("예약 없음"));

        if (req.isApprove()) {
            r.approve();
        } else {
            r.reject(req.getRejectionReason());
        }
    }
}