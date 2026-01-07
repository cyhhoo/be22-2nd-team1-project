package com.mycompany.project.reservation.dto;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Facility {
  private int facilityId;
  private String name;
  private String status;
}
