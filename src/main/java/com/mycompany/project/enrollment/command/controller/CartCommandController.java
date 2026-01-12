package com.mycompany.project.enrollment.command.controller;

import com.mycompany.project.enrollment.command.dto.CartAddRequest;
import com.mycompany.project.enrollment.command.service.CartCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "장바구니 (Cart)", description = "수강 신청 장바구니(예비 수강 신청) 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts")
public class CartCommandController {

  private final CartCommandService cartCommandService;

  // 1. 장바구니 담기
  @Operation(summary = "장바구니 담기", description = "학생이 특정 강좌를 장바구니에 넣습니다.")
  @PostMapping
  public ResponseEntity<Long> addCart(
      @RequestHeader("X-USER-ID") Long userId,
      @Valid @RequestBody CartAddRequest request // [수정] @Valid 추가 (DTO 검증 활성화)
  ) {
    Long cartId = cartCommandService.addCart(userId, request);
    return ResponseEntity.status(201).body(cartId);
  }

  // 2. 장바구니 삭제 (과목 ID 기준)
  @Operation(summary = "장바구니 담기 취소", description = "학생이 장바구니에 넣은 특정 강좌를 삭제합니다.")
  @DeleteMapping("/courses/{courseId}")
  public ResponseEntity<Void> removeCart(
      @RequestHeader("X-USER-ID") Long userId,
      @PathVariable("courseId") Long courseId // [권장] 명시적 매핑
  ) {
    cartCommandService.removeCart(userId, courseId);
    return ResponseEntity.noContent().build();
  }
}