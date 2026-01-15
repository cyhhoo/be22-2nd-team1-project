package com.mycompany.project.reservation.command.application.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

@Getter
@RequiredArgsConstructor
public class ReservationApproveRequest {
    @NotNull
    private final boolean approve;
    private final String rejectionReason;
}
