package com.mycompany.project.reservation.query.repository;

import com.mycompany.project.reservation.query.dto.FacilityDTO;
import com.mycompany.project.reservation.query.dto.ReservationDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Mapper
public interface ReservationMapper {

  // ?덉빟 媛???쒖꽕 議고쉶
  List<FacilityDTO> selectAvailableFacilities(
      @Param("reservationDate") LocalDate reservationDate,
      @Param("startTime") LocalTime startTime
  );

    // ?섏쓽 ?덉빟 議고쉶
    List<ReservationDTO> selectMyReservations(
            @Param("studentId") Long studentId,
            @Param("status") String status
    );

    // 愿由ъ옄 ?덉빟 ?꾪솴 議고쉶 (adminId媛 愿由ы븯???쒖꽕???덉빟 紐⑸줉)
    List<ReservationDTO> selectAdminReservationStatus(
            @Param("adminId") Long adminId,
            @Param("reservationDate") LocalDate reservationDate,
            @Param("status") String status
    );
}