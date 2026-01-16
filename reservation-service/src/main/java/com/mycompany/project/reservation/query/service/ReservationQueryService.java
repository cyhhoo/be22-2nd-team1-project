package com.mycompany.project.reservation.query.service;

import com.mycompany.project.exception.BusinessException;
import com.mycompany.project.exception.ErrorCode;
import com.mycompany.project.reservation.query.dto.FacilityDTO;
import com.mycompany.project.reservation.query.dto.ReservationDTO;
import com.mycompany.project.reservation.query.repository.ReservationMapper;
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

    // Retrieve available facilities for a specific date and time
    public List<FacilityDTO> getAvailableFacilities(LocalDate reservationDate, LocalTime startTime) {
        validateTimeRules(startTime);
        return reservationMapper.selectAvailableFacilities(reservationDate, startTime);
    }

    // Retrieve my reservations (status optional)
    public List<ReservationDTO> getMyReservations(Long studentId, String status) {
        return reservationMapper.selectMyReservations(studentId, status);
    }

    // Admin: Retrieve reservation status (date/status optional)
    public List<ReservationDTO> getAdminReservationStatus(Long adminId, LocalDate reservationDate, String status) {
        return reservationMapper.selectAdminReservationStatus(adminId, reservationDate, status);
    }

    private void validateTimeRules(LocalTime startTime) {
        if (startTime.getMinute() != 0 || startTime.getSecond() != 0) {
            throw new BusinessException(ErrorCode.INVALID_RESERVATION_TIME);
        }
        if (startTime.isBefore(LocalTime.of(6, 0)) || startTime.isAfter(LocalTime.of(20, 0))) {
            throw new BusinessException(ErrorCode.RESERVATION_TIME_OUT_OF_RANGE);
        }
    }
}
