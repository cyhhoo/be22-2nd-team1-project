package com.mycompany.project.reservation.query.service;

import com.mycompany.project.exception.BusinessException;
import com.mycompany.project.exception.ErrorCode;
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

    // 예약 가능 시설 조회
    public List<FacilityDTO> getAvailableFacilities(LocalDate reservationDate, LocalTime startTime) {
        validateTimeRules(startTime);
        return reservationMapper.selectAvailableFacilities(reservationDate, startTime);
    }

    // 나의 예약 조회 (status는 선택)
    public List<ReservationDTO> getMyReservations(Long studentId, String status) {
        return reservationMapper.selectMyReservations(studentId, status);
    }

    // 관리자 예약 현황 조회 (reservationDate/status 선택)
    public List<ReservationDTO> getAdminReservationStatus(Long adminId, LocalDate reservationDate, String status) {
        return reservationMapper.selectAdminReservationStatus(adminId, reservationDate, status);
    }

    private void validateTimeRules(LocalTime startTime) {
        if (startTime.getMinute() != 0 || startTime.getSecond() != 0) {
            throw new BusinessException(ErrorCode.INVALID_RESERVATION_TIME);

        }
        if (startTime.isBefore(LocalTime.of(6, 0)) || startTime.isAfter(LocalTime.of(20, 0))) {
          throw new BusinessException(ErrorCode. RESERVATION_TIME_OUT_OF_RANGE);
        }
    }
}
