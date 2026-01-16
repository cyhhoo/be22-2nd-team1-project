package com.mycompany.project.reservation.command.domain.repository;

import com.mycompany.project.reservation.command.domain.aggregate.Reservation;
import com.mycompany.project.reservation.command.domain.aggregate.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.*;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    boolean existsByFacilityIdAndReservationDateAndStartTime(
            Long facilityId, LocalDate date, LocalTime time);

    boolean existsByFacilityIdAndReservationDateAndStartTimeAndReservationIdNot(
            Long facilityId,
            LocalDate reservationDate,
            LocalTime startTime,
            Long reservationId);

    boolean existsByFacilityIdAndReservationDateAndStartTimeAndStatus(Long facilityId, LocalDate reservationDate,
            LocalTime startTime, ReservationStatus reservationStatus);

}
