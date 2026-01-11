package com.mycompany.project.reservation.query.service;

import com.mycompany.project.reservation.query.dto.FacilityDTO;
import com.mycompany.project.reservation.query.dto.ReservationDTO;
import com.mycompany.project.reservation.query.mapper.ReservationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationQueryService {

    private final ReservationMapper reservationMapper;

    // [RES-02] 예약 가능 시설 조회
    public List<FacilityDTO> getAvailableFacilities(LocalDate reservationDate, LocalTime startTime) {
        validateTimeRules(startTime);
        return reservationMapper.selectAvailableFacilities(reservationDate, startTime);
    }

    // [RES-05] 나의 예약 조회 (status는 선택)
    public List<ReservationDTO> getMyReservations(Long studentId, String status) {
        return reservationMapper.selectMyReservations(studentId, status);
    }

    // [RES-08] 관리자 예약 현황 조회 (reservationDate/status 선택)
    public List<ReservationDTO> getAdminReservationStatus(Long adminId, LocalDate reservationDate, String status) {
        return reservationMapper.selectAdminReservationStatus(adminId, reservationDate, status);
    }

    private void validateTimeRules(LocalTime startTime) {
        if (startTime.getMinute() != 0 || startTime.getSecond() != 0) {
            throw new IllegalStateException("예약은 1시간 단위(정각)로만 가능합니다.");
        }
        if (startTime.isBefore(LocalTime.of(6, 0)) || startTime.isAfter(LocalTime.of(20, 0))) {
            throw new IllegalStateException("예약 가능 시간은 06:00~21:00이며 시작은 20:00까지 가능합니다.");
        }
    }
}
