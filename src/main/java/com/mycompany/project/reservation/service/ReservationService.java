package com.mycompany.project.reservation.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import com.mycompany.project.reservation.repository.ReservationRepository;
import com.mycompany.project.reservation.mapper.ReservationMapper;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;
}
