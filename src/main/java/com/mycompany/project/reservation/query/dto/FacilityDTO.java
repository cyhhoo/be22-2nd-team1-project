package com.mycompany.project.reservation.query.dto;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FacilityDTO {
  private int facilityId;
  private String name;
  private String status;
}
