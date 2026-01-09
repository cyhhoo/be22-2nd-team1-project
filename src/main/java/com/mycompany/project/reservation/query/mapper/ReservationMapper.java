package com.mycompany.project.reservation.query.mapper;

import com.mycompany.project.reservation.query.dto.FacilityDTO;
import com.mycompany.project.reservation.query.dto.ReservationDTO;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationMapper {

    List<FacilityDTO> selectAvailableFacilities(
        @Param("startTime") LocalDateTime startTime
    );

    List<ReservationDTO> selectMyReservations(
        @Param("studentId") int studentId,
        @Param("status") String status
    );

    List<ReservationDTO> selectReservationList(
        @Param("studentId") Integer studentId,
        @Param("status") String status
    );
}
