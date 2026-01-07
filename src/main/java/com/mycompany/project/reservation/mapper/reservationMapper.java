package com.mycompany.project.reservation.mapper;

import com.mycompany.project.reservation.dto.ReservationDTO;

import java.util.List;

public interface reservationMapper {
  List<ReservationDTO> selectAvailableFacilities(String status);

}
