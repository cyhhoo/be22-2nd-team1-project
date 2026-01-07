package com.mycompany.project.reservation.dto;

import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReservationDTO {
  private int reservationId;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private String status;
  private LocalDateTime createdAt;
  private String rejectionReason;
}
