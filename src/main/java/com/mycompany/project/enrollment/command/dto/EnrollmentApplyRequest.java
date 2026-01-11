package com.mycompany.project.enrollment.command.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EnrollmentApplyRequest {

  @NotNull(message = "강좌 Id는 필수")
  private Long courseId;

}
