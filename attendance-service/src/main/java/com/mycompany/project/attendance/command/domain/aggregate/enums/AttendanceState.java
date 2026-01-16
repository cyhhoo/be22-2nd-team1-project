package com.mycompany.project.attendance.command.domain.aggregate.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AttendanceState {
    SAVED("Saved"),
    CONFIRMED("Confirmed"),
    CLOSED("Closed");

    private final String description;
}
