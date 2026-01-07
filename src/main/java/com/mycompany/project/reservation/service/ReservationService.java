package com.mycompany.project.reservation.service;

import com.mycompany.project.reservation.dto.ReservationDTO;
import com.mycompany.project.reservation.mapper.reservationMapper;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import com.mycompany.project.reservation.repository.ReservationRepository;

import java.util.List;


@Service
public class ReservationService {
  private final ReservationRepository reservationRepository;


  public ReservationService(ReservationRepository reservationRepository) {
    this.reservationRepository = reservationRepository;
  }

}
