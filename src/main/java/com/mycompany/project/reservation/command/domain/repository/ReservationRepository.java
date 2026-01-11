package com.mycompany.project.reservation.command.domain.repository;

import com.mycompany.project.reservation.command.domain.aggregate.Reservation;

import java.util.Optional;

public interface ReservationRepository {
  Reservation save(Reservation reservation);

  Optional<Reservation> findById(int reservationId);
}
