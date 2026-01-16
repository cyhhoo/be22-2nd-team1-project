package com.mycompany.project.reservation.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(name = "tbl_facility")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Facility {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "facility_id")
  private Long facilityId;

  @Column(nullable = false, length = 50)
  private String name;

  // Stored as string from FacilityStatus enum validates
  @Column(nullable = false, length = 20)
  private String status;

  @Column(name = "open_time", nullable = false)
  private LocalTime openTime;

  @Column(name = "close_time", nullable = false)
  private LocalTime closeTime;

  @Column(name = "admin_id", nullable = false)
  private Long adminId;

  @Column(nullable = false, length = 100)
  private String location;

  @Column(name = "facility_type", nullable = false, length = 50)
  private String facilityType;

  public boolean isAvailable() {
    return FacilityStatus.AVAILABLE.name().equals(status);
  }

  // Update facility information
  public void update(
      String name,
      FacilityStatus status,
      LocalTime openTime,
      LocalTime closeTime,
      String location,
      String facilityType) {
    this.name = name;
    this.status = status.name();
    this.openTime = openTime;
    this.closeTime = closeTime;
    this.location = location;
    this.facilityType = facilityType;
  }
}
