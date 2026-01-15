package com.mycompany.project.reservation.command.application.dto.request;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FacilityChangeRequest {

  @NotNull
  private final String name;
  @NotNull
  private final String status;
}
