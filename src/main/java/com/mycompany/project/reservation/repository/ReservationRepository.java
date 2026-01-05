package com.mycompany.project.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mycompany.project.reservation.entity.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
