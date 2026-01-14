package com.mycompany.project.reservation.query.mapper;

import com.mycompany.project.reservation.query.dto.FacilityDTO;
import com.mycompany.project.reservation.query.dto.ReservationDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Mapper
public interface ReservationMapper {

  // 예약 가능 시설 조회
  List<FacilityDTO> selectAvailableFacilities(
      @Param("reservationDate") LocalDate reservationDate,
      @Param("startTime") LocalTime startTime
  );

    // 나의 예약 조회
    List<ReservationDTO> selectMyReservations(
            @Param("studentId") Long studentId,
            @Param("status") String status
    );

    // 관리자 예약 현황 조회 (adminId가 관리하는 시설의 예약 목록)
    List<ReservationDTO> selectAdminReservationStatus(
            @Param("adminId") Long adminId,
            @Param("reservationDate") LocalDate reservationDate,
            @Param("status") String status
    );
}