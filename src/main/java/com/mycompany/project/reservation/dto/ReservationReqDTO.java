package com.mycompany.project.reservation.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ReservationReqDTO {
    private Long targetId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
