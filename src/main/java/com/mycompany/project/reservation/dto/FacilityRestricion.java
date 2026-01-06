package com.mycompany.project.reservation.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FacilityRestricion {
  private int restrictionId;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private String reason;
  private LocalDateTime createdAt;


}
