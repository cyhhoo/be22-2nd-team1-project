package com.mycompany.project.attendance.command.application.dto;

import com.mycompany.project.attendance.command.domain.aggregate.enums.CorrectionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CorrectionSearchRequest {
    private Long attendanceId;
    private Long requestedBy;
    private CorrectionStatus status;
    private LocalDateTime fromDateTime;
    private LocalDateTime toDateTime;
    private Integer limit;
    private Integer offset;
}
