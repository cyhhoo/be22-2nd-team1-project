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
import com.mycompany.project.reservation.command.domain.repository.FacilityRepository;
import com.mycompany.project.reservation.command.domain.repository.FacilityRestrictionRepository;
import com.mycompany.project.reservation.command.domain.repository.ReservationRepository;
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

  /* Create Facility Reservation */
  public ReservationCommandResponse create(Long studentId, ReservationCreateRequest req) {

    // 1) Check facility existência/availability
    Facility facility = facilityRepository.findById(req.getFacilityId())
        .orElseThrow(() -> new BusinessException(ErrorCode.FACILITY_NOT_FOUND));

    if (!facility.isAvailable()) {
      throw new BusinessException(ErrorCode.FACILITY_NOT_AVAILABLE);
    }

    // 2) Validate time rules and facility open hours
    validateTimeRules(req.getStartTime());
    validateFacilityOpenHours(facility, req.getStartTime());

    // 3) Check for facility restrictions on specified date
    validateRestriction(req.getFacilityId(), req.getReservationDate());

    // 4) Check for duplicate reservations (primary check)
    if (reservationRepository.existsByFacilityIdAndReservationDateAndStartTime(
        req.getFacilityId(), req.getReservationDate(), req.getStartTime())) {
      throw new BusinessException(ErrorCode.RESERVED_TIME_CONFLICT);
    }

    // 5) Create and initialize reservation
    Reservation reservation = Reservation.create(
        req.getFacilityId(), studentId, req.getReservationDate(), req.getStartTime());

    // 6) Final persistent check
    try {
      return ReservationCommandResponse.from(reservationRepository.save(reservation));
    } catch (DataIntegrityViolationException e) {
      throw new BusinessException(ErrorCode.RESERVED_TIME_CONFLICT);
    }
  }

  /* Cancel Facility Reservation */
  public void cancel(Long reservationId, Long studentId) {
    Reservation r = reservationRepository.findById(reservationId)
        .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));

    if (!r.getStudentId().equals(studentId)) {
      throw new BusinessException(ErrorCode.NOT_RESERVATION_OWNER);
    }

    r.cancel();
  }

  /* Change Facility Reservation */
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

    // Check if new date/time is same as original
    boolean sameDateTime = Objects.equals(r.getReservationDate(), req.getReservationDate()) &&
        Objects.equals(r.getStartTime(), req.getStartTime());

    // Perform duplicate check only if date/time changed
    if (!sameDateTime) {
      boolean duplicated = reservationRepository
          .existsByFacilityIdAndReservationDateAndStartTimeAndReservationIdNot(
              r.getFacilityId(),
              req.getReservationDate(),
              req.getStartTime(),
              r.getReservationId());

      if (duplicated) {
        throw new BusinessException(ErrorCode.RESERVED_TIME_CONFLICT);
      }
    }

    r.change(req.getReservationDate(), req.getStartTime());
  }

  /* Admin Approval/Rejection */
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
              r.getFacilityId(), r.getReservationDate(), r.getStartTime(), ReservationStatus.APPROVED);

      if (existsApproved) {
        throw new BusinessException(ErrorCode.ALREADY_APPROVED_RESERVATION);
      }

      r.approve();
    } else {
      r.reject(req.getRejectionReason());
    }
  }

  /* Validation Methods */

  private void validateRestriction(Long facilityId, LocalDate date) {
    boolean restricted = restrictionRepository.existsByFacilityIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
        facilityId, date, date);
    if (restricted)
      throw new BusinessException(ErrorCode.FACILITY_RESTRICTED_PERIOD);
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