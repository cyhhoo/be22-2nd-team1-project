package com.mycompany.project.reservation.query.service;

import com.mycompany.project.reservation.query.dto.ReservationDTO;
import com.mycompany.project.reservation.query.dto.FacilityDTO;
import com.mycompany.project.reservation.query.repository.ReservationMapper;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationMapper reservationMapper;

    //
    public List<FacilityDTO> selectAvailableFacilities(LocalDateTime startTime) {
        return reservationMapper.selectAvailableFacilities(startTime.toLocalDate(), startTime.toLocalTime());
    }


    // ?섏쓽 ?덉빟 議고쉶
    public List<ReservationDTO> selectMyReservations(Long studentId, String status) {
        return reservationMapper.selectMyReservations(studentId, status);
    }
}