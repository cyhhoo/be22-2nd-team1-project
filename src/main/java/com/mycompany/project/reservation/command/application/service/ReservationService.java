package com.mycompany.project.reservation.command.application.service;

import com.mycompany.project.reservation.command.application.dto.request.ReservationCreateRequest;
import com.mycompany.project.reservation.command.application.dto.response.ReservationCommandResponse;
import com.mycompany.project.reservation.command.domain.aggregate.Facility;
import com.mycompany.project.reservation.command.domain.aggregate.Reservation;
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
public class ReservationService {

  private final ReservationRepository reservationRepository;
  private final FacilityRepository facilityRepository;
  private final FacilityRestrictionRepository restrictionRepository;

  @Transactional
  public ReservationCommandResponse create(Long studentId, ReservationCreateRequest req) {

    Facility facility = facilityRepository.findById(req.getFacilityId())
            .orElseThrow(() -> new IllegalStateException("시설이 존재하지 않습니다."));

    if (!facility.isAvailable()) {
      throw new IllegalStateException("예약 불가 시설입니다.");
    }

    validateTimeRules(req.getStartTime());                // 06~21, 정각, 1시간단위
    validateFacilityOpenHours(facility, req.getStartTime());
    validateRestriction(req.getFacilityId(), req.getReservationDate());

    if (reservationRepository.existsByFacilityIdAndReservationDateAndStartTime(
            req.getFacilityId(), req.getReservationDate(), req.getStartTime())) {
      throw new IllegalStateException("이미 예약된 시간입니다.");
    }

    Reservation reservation = Reservation.create(
            req.getFacilityId(), studentId, req.getReservationDate(), req.getStartTime()
    );

    try {
      return ReservationCommandResponse.from(reservationRepository.save(reservation));
    } catch (DataIntegrityViolationException e) {
      throw new IllegalStateException("이미 예약된 시간입니다.");
    }
  }

  private void validateRestriction(Long facilityId, LocalDate date) {
    boolean restricted =
            restrictionRepository.existsByFacilityIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                    facilityId, date, date);
    if (restricted) throw new IllegalStateException("시설 이용 제한 기간입니다.");
  }

  private void validateTimeRules(LocalTime startTime) {
    if (startTime.getMinute() != 0 || startTime.getSecond() != 0) {
      throw new IllegalStateException("예약은 1시간 단위(정각)로만 가능합니다.");
    }
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
