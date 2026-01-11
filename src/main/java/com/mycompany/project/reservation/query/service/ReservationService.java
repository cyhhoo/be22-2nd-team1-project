package com.mycompany.project.reservation.query.service;

import com.mycompany.project.reservation.query.dto.ReservationDTO;
import com.mycompany.project.reservation.query.dto.FacilityDTO;
import com.mycompany.project.reservation.query.mapper.ReservationMapper;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationMapper reservationMapper;

    public List<FacilityDTO> selectAvailableFacilities(LocalDateTime startTime) {
        return reservationMapper.selectAvailableFacilities(startTime);
    }

    public List<ReservationDTO> selectMyReservations(int studentId, String status) {
        return reservationMapper.selectMyReservations(studentId, status);
    }
}