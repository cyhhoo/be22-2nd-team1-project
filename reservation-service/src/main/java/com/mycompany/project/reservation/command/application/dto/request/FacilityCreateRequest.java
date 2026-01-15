package com.mycompany.project.reservation.command.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalTime;

@Getter
@RequiredArgsConstructor
public class FacilityCreateRequest {

  @NotBlank
  private String name;

  @NotNull
  private LocalTime openTime;

  @NotNull
  private LocalTime closeTime;

  @NotBlank
  private String location;

  @NotBlank
  private String facilityType;


}
