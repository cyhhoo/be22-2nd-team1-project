package com.mycompany.project.reservation.command.application.dto.response;

import com.mycompany.project.reservation.command.domain.aggregate.Facility;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@Builder
public class FacilityCommandResponse {

  private Long facilityId;
  private String name;
  private String status;
  private LocalTime openTime;
  private LocalTime closeTime;
  private String location;
  private String facilityType;
  private Long adminId;

  public static FacilityCommandResponse from(Facility f) {
    return FacilityCommandResponse.builder()
        .facilityId(f.getFacilityId())
        .name(f.getName())
        .status(f.getStatus())
        .openTime(f.getOpenTime())
        .closeTime(f.getCloseTime())
        .location(f.getLocation())
        .facilityType(f.getFacilityType())
        .adminId(f.getAdminId())
        .build();
  }
}

