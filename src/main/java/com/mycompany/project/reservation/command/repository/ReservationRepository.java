package com.mycompany.project.reservation.command.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mycompany.project.reservation.command.entity.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
