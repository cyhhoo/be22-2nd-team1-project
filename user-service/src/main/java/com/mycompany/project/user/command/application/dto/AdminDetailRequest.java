package com.mycompany.project.user.command.application.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdminDetailRequest {
    private String level; // "1" or "5" (LEVEL_1, LEVEL_5)
}
