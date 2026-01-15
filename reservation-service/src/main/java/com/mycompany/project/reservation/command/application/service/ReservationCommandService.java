package com.mycompany.project.reservation.command.application.service;

import com.mycompany.project.exception.BusinessException;
import com.mycompany.project.exception.ErrorCode;
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
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationCommandService {

  private final ReservationRepository reservationRepository;
  private final FacilityRepository facilityRepository;
  private final FacilityRestrictionRepository restrictionRepository;

  /* 시설 예약 신청 */
  public ReservationCommandResponse create(Long studentId, ReservationCreateRequest req) {

    // 1) 시설 존재/상태 확인
    Facility facility = facilityRepository.findById(req.getFacilityId())
        .orElseThrow(() -> new BusinessException(ErrorCode.FACILITY_NOT_FOUND));

    if (!facility.isAvailable()) {
      throw new BusinessException(ErrorCode.FACILITY_NOT_AVAILABLE);
    }

    // 2) 시간 규칙 + 시설 운영시간 확인
    validateTimeRules(req.getStartTime());
    validateFacilityOpenHours(facility, req.getStartTime());

    // 3) 시설 이용 제한 기간 확인
    validateRestriction(req.getFacilityId(), req.getReservationDate());

    // 4) 중복 예약 확인 (1차)
    if (reservationRepository.existsByFacilityIdAndReservationDateAndStartTime(
        req.getFacilityId(), req.getReservationDate(), req.getStartTime())) {
      throw new BusinessException(ErrorCode.RESERVED_TIME_CONFLICT);
    }

    // 5) 예약 생성/저장
    Reservation reservation = Reservation.create(
        req.getFacilityId(), studentId, req.getReservationDate(), req.getStartTime()
    );

    // 6) 동시성 최종 방어
    try {
      return ReservationCommandResponse.from(reservationRepository.save(reservation));
    } catch (DataIntegrityViolationException e) {
      throw new BusinessException(ErrorCode.RESERVED_TIME_CONFLICT);
    }
  }

  /* 시설 예약 취소 */
  public void cancel(Long reservationId, Long studentId) {
    Reservation r = reservationRepository.findById(reservationId)
        .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));

    if (!r.getStudentId().equals(studentId)) {
      throw new BusinessException(ErrorCode.NOT_RESERVATION_OWNER);
    }

    r.cancel();
  }

  /* 시설 예약 변경 */
  public void change(Long reservationId, Long studentId, ReservationChangeRequest req) {
    Reservation r = reservationRepository.findById(reservationId)
        .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));

    if (!r.getStudentId().equals(studentId)) {
      throw new BusinessException(ErrorCode.NOT_RESERVATION_OWNER);
    }

    validateTimeRules(req.getStartTime());
    validateRestriction(r.getFacilityId(), req.getReservationDate());

    Facility facility = facilityRepository.findById(r.getFacilityId())
        .orElseThrow(() -> new BusinessException(ErrorCode.FACILITY_NOT_FOUND));
    validateFacilityOpenHours(facility, req.getStartTime());

    // 변경 전과 동일한 날짜/시간인지 체크
    boolean sameDateTime =
        Objects.equals(r.getReservationDate(), req.getReservationDate()) &&
            Objects.equals(r.getStartTime(), req.getStartTime());

    // 실제로 변경되는 경우에만 중복 체크
    if (!sameDateTime) {
      boolean duplicated =
          reservationRepository
              .existsByFacilityIdAndReservationDateAndStartTimeAndIdNot(
                  r.getFacilityId(),
                  req.getReservationDate(),
                  req.getStartTime(),
                  r.getStudentId()
              );

      if (duplicated) {
        throw new BusinessException(ErrorCode.RESERVED_TIME_CONFLICT);
      }
    }

    r.change(req.getReservationDate(), req.getStartTime());
  }

  /* 관리자 승인/거부 */
  public void approve(Long adminId, Long reservationId, ReservationApproveRequest req) {
    Reservation r = reservationRepository.findById(reservationId)
        .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));

    Facility facility = facilityRepository.findById(r.getFacilityId())
        .orElseThrow(() -> new BusinessException(ErrorCode.FACILITY_NOT_FOUND));

    if (!facility.getAdminId().equals(adminId)) {
      throw new BusinessException(ErrorCode.NOT_FACILITY_ADMIN);
    }

    if (req.isApprove()) {
      boolean existsApproved = reservationRepository
          .existsByFacilityIdAndReservationDateAndStartTimeAndStatus(
              r.getFacilityId(), r.getReservationDate(), r.getStartTime(), ReservationStatus.APPROVED
          );

      if (existsApproved) {
        throw new BusinessException(ErrorCode.ALREADY_APPROVED_RESERVATION);
      }

      r.approve();
    } else {
      r.reject(req.getRejectionReason());
    }
  }

  /* 검증 메서드들 */

  private void validateRestriction(Long facilityId, LocalDate date) {
    boolean restricted = restrictionRepository.existsByFacilityIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
        facilityId, date, date
    );
    if (restricted) throw new BusinessException(ErrorCode.FACILITY_RESTRICTED_PERIOD);
  }

  private void validateTimeRules(LocalTime startTime) {
    if (startTime.getMinute() != 0 || startTime.getSecond() != 0) {
      throw new BusinessException(ErrorCode.INVALID_RESERVATION_TIME);
    }
    if (startTime.isBefore(LocalTime.of(6, 0)) || startTime.isAfter(LocalTime.of(20, 0))) {
      throw new BusinessException(ErrorCode.RESERVATION_TIME_OUT_OF_RANGE);
    }
  }

  private void validateFacilityOpenHours(Facility facility, LocalTime startTime) {
    if (startTime.isBefore(facility.getOpenTime()) || !startTime.isBefore(facility.getCloseTime())) {
      throw new BusinessException(ErrorCode.FACILITY_OUT_OF_OPERATION_HOURS);
    }
  }
}