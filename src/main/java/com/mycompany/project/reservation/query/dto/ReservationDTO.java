package com.mycompany.project.reservation.query.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReservationDTO {
<<<<<<< HEAD
  private Long reservationId;
  private Long facilityId;
  private Long studentId;
  private LocalDate reservationDate;
  private LocalTime startTime;
  private LocalTime endTime;
  private String status;
  private LocalDateTime createdAt;
  private String rejectionReason;
=======
    private Long reservationId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private LocalDateTime createdAt;
    private String rejectionReason;
>>>>>>> c903d997dca1c36896004362fb3f750d68730251
}
