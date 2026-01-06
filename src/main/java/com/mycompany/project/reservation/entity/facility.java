package com.mycompany.project.reservation.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_facility")
@Getter
@NoArgsConstructor
public class facility {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int facilityId;
    private String name;
    private String status;
}
