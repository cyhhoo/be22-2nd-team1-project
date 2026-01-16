package com.mycompany.project.enrollment.command.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CartAddRequest {

  @NotNull(message = "媛뺤쥖 Id???꾩닔")
  private Long courseId;

}
