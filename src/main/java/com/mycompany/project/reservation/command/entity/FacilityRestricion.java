package com.mycompany.project.reservation.command.entity;

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
