package com.mycompany.project.reservation.command.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalTime;

@Getter
public class FacilityUpdateRequest {


  @NotBlank
  private String name;

  @NotBlank
  private String status; // AVAILABLE / UNAVAILABLE

  @NotNull
  private LocalTime openTime;

  @NotNull
  private LocalTime closeTime;

  @NotBlank
  private String location;

  @NotBlank
  private String facilityType;
}


