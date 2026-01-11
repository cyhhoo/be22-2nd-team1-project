package com.mycompany.project.reservation.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(
        name = "tbl_reservation",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"facility_id", "reservation_date", "start_time"}
        )
)
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

  /* 생성 */
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

  /* 취소 */
  public void cancel() {
    if (status == ReservationStatus.APPROVED) {
      throw new IllegalStateException("승인된 예약은 취소할 수 없습니다.");
    }
    this.status = ReservationStatus.CANCELED;
  }

  /* 변경 */
  public void change(LocalDate date, LocalTime startTime) {
    if (status != ReservationStatus.WAITING) {
      throw new IllegalStateException("대기 상태만 변경 가능합니다.");
    }
    this.reservationDate = date;
    this.startTime = startTime;
    this.endTime = startTime.plusHours(1);
  }

  /* 승인 */
  public void approve() {
    if (status != ReservationStatus.WAITING) {
      throw new IllegalStateException("이미 처리된 예약입니다.");
    }
    this.status = ReservationStatus.APPROVED;
  }

  /* 거부 */
  public void reject(String reason) {
    if (status != ReservationStatus.WAITING) {
      throw new IllegalStateException("이미 처리된 예약입니다.");
    }
    this.status = ReservationStatus.REJECTED;
    this.rejectionReason = reason;
  }
}