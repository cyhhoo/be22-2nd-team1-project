package com.mycompany.project.enrollment.command.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "", description = "")
@RestController
@RequestMapping("/api/")
public class CartCommandController {


  @Operation(summary = "장바구니 담기", description = "학생이 특정 강좌를 장바구니에 넣습니다.")
  @PostMapping("/{courseId}")
  public void cart(@PathVariable Long courseId){

  }

  @Operation(summary = "장바구니 담기", description = "학생이 특정 강좌를 장바구니에 넣습니다.")
  @DeleteMapping("/{courseId}")
  public void cancel(@PathVariable Long courseId){

  }

}
