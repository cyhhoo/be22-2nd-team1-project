package com.mycompany.project.reservation.command.application.dto.response;

import com.mycompany.project.reservation.command.domain.aggregate.Reservation;
import com.mycompany.project.reservation.command.domain.aggregate.ReservationStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReservationCommandResponse {
  private Long reservationId;
  private ReservationStatus status;

  public static ReservationCommandResponse from(Reservation r) {
    return ReservationCommandResponse.builder()
            .reservationId(r.getReservationId())
            .status(r.getStatus())
            .build();
  }
}