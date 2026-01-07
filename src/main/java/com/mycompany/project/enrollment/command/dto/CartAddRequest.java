package com.mycompany.project.enrollment.command.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class CartAddRequest {

  @NotNull(message = "강좌 Id는 필수")
  private Long courseId;

}
