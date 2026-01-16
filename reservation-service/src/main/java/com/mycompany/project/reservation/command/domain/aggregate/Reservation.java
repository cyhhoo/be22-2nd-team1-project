package com.mycompany.project.reservation.command.domain.aggregate;

import com.mycompany.project.exception.BusinessException;
import com.mycompany.project.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "tbl_reservation", uniqueConstraints = @UniqueConstraint(columnNames = { "facility_id",
    "reservation_date", "start_time" }))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Reservation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long reservationId;

  private Long facilityId;
  private Long studentId;

  private LocalTime startTime;
  private LocalTime endTime;

  @Enumerated(EnumType.STRING)
  private ReservationStatus status;

  private LocalDate reservationDate;
  private LocalDateTime createdAt;
  private String rejectionReason;

  /* Creation */
  public static Reservation create(Long facilityId, Long studentId, LocalDate date, LocalTime startTime) {
    return Reservation.builder()
        .facilityId(facilityId)
        .studentId(studentId)
        .reservationDate(date)
        .startTime(startTime)
        .endTime(startTime.plusHours(1))
        .status(ReservationStatus.WAITING)
        .createdAt(LocalDateTime.now())
        .build();
  }

  /* Cancellation */
  public void cancel() {
    if (status == ReservationStatus.APPROVED) {
      throw new BusinessException(ErrorCode.RESERVATION_APPROVED_CANNOT_CANCEL);
    }
    this.status = ReservationStatus.CANCELED;
  }

  /* Modification */
  public void change(LocalDate date, LocalTime startTime) {
    if (status != ReservationStatus.WAITING) {
      throw new BusinessException(ErrorCode.RESERVATION_ONLY_WAITING_CAN_CHANGE);
    }
    this.reservationDate = date;
    this.startTime = startTime;
    this.endTime = startTime.plusHours(1);
  }

  /* Approval */
  public void approve() {
    if (status != ReservationStatus.WAITING) {
      throw new BusinessException(ErrorCode.RESERVATION_ALREADY_PROCESSED);
    }
    this.status = ReservationStatus.APPROVED;
  }

  /* Rejection */
  public void reject(String reason) {
    if (status != ReservationStatus.WAITING) {
      throw new BusinessException(ErrorCode.RESERVATION_ALREADY_PROCESSED);
    }
    this.status = ReservationStatus.REJECTED;
    this.rejectionReason = reason;
  }
}