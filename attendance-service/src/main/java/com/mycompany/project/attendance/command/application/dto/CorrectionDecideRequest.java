package com.mycompany.project.attendance.command.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CorrectionDecideRequest {
    private Long requestId;
    private boolean approved;
    private String adminComment;
    private Long adminId;
}
