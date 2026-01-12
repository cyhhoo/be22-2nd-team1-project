package com.mycompany.project.reservation.command.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@RequiredArgsConstructor
public class ReservationCreateRequest {
  @NotNull
  private final Long facilityId;
  @NotNull
  private final LocalDate reservationDate;
  @NotNull
  private final LocalTime startTime;
}