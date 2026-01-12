<<<<<<< HEAD:src/main/java/com/mycompany/project/reservation/command/domain/aggregate/FacilityRestricion.java
package com.mycompany.project.reservation.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_facility_restriction")
@Getter
@NoArgsConstructor
public class FacilityRestricion {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int restrictionId;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private String reason;
  private LocalDateTime createdAt;
}
=======
//package com.mycompany.project.reservation.command.entity;
//
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "tbl_facility_restriction")
//@Getter
//@NoArgsConstructor
//public class FacilityRestricion {
//  @Id
//  @GeneratedValue(strategy = GenerationType.IDENTITY)
//  private int restrictionId;
//  private LocalDateTime startDate;
//  private LocalDateTime endDate;
//  private String reason;
//  private LocalDateTime createdAt;
//}
>>>>>>> c903d997dca1c36896004362fb3f750d68730251:src/main/java/com/mycompany/project/reservation/command/entity/FacilityRestricion.java
