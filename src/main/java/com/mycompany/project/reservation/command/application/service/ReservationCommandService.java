package com.mycompany.project.reservation.command.application.service;

import com.mycompany.project.reservation.command.application.dto.request.ReservationApproveRequest;
import com.mycompany.project.reservation.command.application.dto.request.ReservationChangeRequest;
import com.mycompany.project.reservation.command.application.dto.request.ReservationCreateRequest;
import com.mycompany.project.reservation.command.application.dto.response.ReservationCommandResponse;
import com.mycompany.project.reservation.command.domain.aggregate.Facility;
import com.mycompany.project.reservation.command.domain.aggregate.Reservation;
import com.mycompany.project.reservation.command.domain.aggregate.ReservationStatus;
import com.mycompany.project.reservation.command.infrastructure.repository.FacilityRepository;
import com.mycompany.project.reservation.command.infrastructure.repository.FacilityRestrictionRepository;
import com.mycompany.project.reservation.command.infrastructure.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationCommandService {

    private final ReservationRepository reservationRepository;
    private final FacilityRepository facilityRepository;
    private final FacilityRestrictionRepository restrictionRepository;

    /* =========================
     * [RES-01] 시설 예약 신청
     * ========================= */
    public ReservationCommandResponse create(Long studentId, ReservationCreateRequest req) {

        // 1) 시설 존재/상태 확인
        Facility facility = facilityRepository.findById(req.getFacilityId())
                .orElseThrow(() -> new IllegalStateException("시설이 존재하지 않습니다."));

        if (!facility.isAvailable()) {
            throw new IllegalStateException("예약 불가 시설입니다.");
        }

        // 2) 시간 규칙(정각, 06~21) + 시설 운영시간 확인
        validateTimeRules(req.getStartTime());
        validateFacilityOpenHours(facility, req.getStartTime());

        // 3) 시설 이용 제한 기간 확인
        validateRestriction(req.getFacilityId(), req.getReservationDate());

        // 4) 중복 예약 확인 (1차)
        if (reservationRepository.existsByFacilityIdAndReservationDateAndStartTime(
                req.getFacilityId(), req.getReservationDate(), req.getStartTime())) {
            throw new IllegalStateException("이미 예약된 시간입니다.");
        }

        // 5) 예약 생성/저장
        Reservation reservation = Reservation.create(
                req.getFacilityId(), studentId, req.getReservationDate(), req.getStartTime()
        );

        // 6) 동시성 최종 방어(UNIQUE 충돌)
        try {
            return ReservationCommandResponse.from(reservationRepository.save(reservation));
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("이미 예약된 시간입니다.");
        }
    }

    /* =========================
     * [RES-03] 시설 예약 취소
     * ========================= */
    public void cancel(Long reservationId, Long studentId) {
        Reservation r = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalStateException("예약 없음"));

        if (!r.getStudentId().equals(studentId)) {
            throw new IllegalStateException("본인 예약만 취소 가능");
        }

        r.cancel();
    }

    /* =========================
     * [RES-04] 시설 예약 변경
     * ========================= */
    public void change(Long reservationId, Long studentId, ReservationChangeRequest req) {
        Reservation r = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalStateException("예약 없음"));

        if (!r.getStudentId().equals(studentId)) {
            throw new IllegalStateException("본인 예약만 변경 가능");
        }

        // 변경하려는 시간 규칙 체크
        validateTimeRules(req.getStartTime());

        // 변경하려는 날짜가 제한 기간인지 체크
        validateRestriction(r.getFacilityId(), req.getReservationDate());

        // 시설 운영시간 체크
        Facility facility = facilityRepository.findById(r.getFacilityId())
                .orElseThrow(() -> new IllegalStateException("시설이 존재하지 않습니다."));
        validateFacilityOpenHours(facility, req.getStartTime());

        // 중복 예약 확인
        if (reservationRepository.existsByFacilityIdAndReservationDateAndStartTime(
                r.getFacilityId(), req.getReservationDate(), req.getStartTime())) {
            throw new IllegalStateException("이미 예약된 시간입니다.");
        }

        r.change(req.getReservationDate(), req.getStartTime());
    }

    /* =========================
     * [RES-06/RES-09] 관리자 승인/거부
     * - adminId는 컨트롤러에서 @RequestParam 으로 받는다고 가정
     * - RES-09 승인 중복 방지 포함
     * ========================= */
    public void approve(Long adminId, Long reservationId, ReservationApproveRequest req) {
        Reservation r = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalStateException("예약 없음"));

        // (선택) adminId 권한 체크를 하려면, 시설의 admin_id와 비교해야 함
        Facility facility = facilityRepository.findById(r.getFacilityId())
                .orElseThrow(() -> new IllegalStateException("시설이 존재하지 않습니다."));

        if (!facility.getAdminId().equals(adminId)) {
            throw new IllegalStateException("해당 시설의 관리자만 승인/거부할 수 있습니다.");
        }

        if (req.isApprove()) {
            // RES-09: 동일 시설/날짜/시간에 이미 APPROVED 있으면 승인 불가
            boolean existsApproved = reservationRepository
                    .existsByFacilityIdAndReservationDateAndStartTimeAndStatus(
                            r.getFacilityId(), r.getReservationDate(), r.getStartTime(), ReservationStatus.APPROVED
                    );

            if (existsApproved) {
                throw new IllegalStateException("동일 시간에 이미 승인된 예약이 존재합니다.");
            }

            r.approve();
        } else {
            r.reject(req.getRejectionReason());
        }
    }

    /* =========================
     * 검증 메서드들
     * ========================= */

    private void validateRestriction(Long facilityId, LocalDate date) {
        boolean restricted =
                restrictionRepository.existsByFacilityIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        facilityId, date, date
                );
        if (restricted) throw new IllegalStateException("시설 이용 제한 기간입니다.");
    }

    private void validateTimeRules(LocalTime startTime) {
        // 1시간 단위: 정각만
        if (startTime.getMinute() != 0 || startTime.getSecond() != 0) {
            throw new IllegalStateException("예약은 1시간 단위(정각)로만 가능합니다.");
        }
        // 06:00~21:00, 시작은 20:00까지
        if (startTime.isBefore(LocalTime.of(6, 0)) || startTime.isAfter(LocalTime.of(20, 0))) {
            throw new IllegalStateException("예약 가능 시간은 06:00~21:00이며 시작은 20:00까지 가능합니다.");
        }
    }

    private void validateFacilityOpenHours(Facility facility, LocalTime startTime) {
        if (startTime.isBefore(facility.getOpenTime()) || !startTime.isBefore(facility.getCloseTime())) {
            throw new IllegalStateException("시설 운영시간 밖입니다.");
        }
    }
}