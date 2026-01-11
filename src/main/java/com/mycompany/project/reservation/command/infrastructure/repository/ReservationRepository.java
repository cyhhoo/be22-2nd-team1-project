package com.mycompany.project.reservation.command.infrastructure.repository;

import com.mycompany.project.reservation.command.domain.aggregate.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.*;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    boolean existsByFacilityIdAndReservationDateAndStartTime(
            Long facilityId, LocalDate date, LocalTime time
    );
}
