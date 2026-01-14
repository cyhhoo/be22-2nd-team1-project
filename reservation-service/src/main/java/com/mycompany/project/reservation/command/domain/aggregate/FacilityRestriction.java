package com.mycompany.project.reservation.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_facility_restriction")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class FacilityRestriction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "restriction_id")
    private Long restrictionId;

    @Column(name = "facility_id", nullable = false)
    private Long facilityId;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(nullable = false, length = 255)
    private String reason;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
