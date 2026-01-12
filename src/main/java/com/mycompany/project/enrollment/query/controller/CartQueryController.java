package com.mycompany.project.enrollment.query.controller;

import com.mycompany.project.enrollment.query.dto.CartListResponse;
import com.mycompany.project.enrollment.query.service.CartQueryService;
import io.swagger.v3.oas.annotations.Operation; // [추가]
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Tag(name = "장바구니 (Cart)", description = "장바구니 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts")
public class CartQueryController {

  private final CartQueryService cartQueryService;


  @Operation(summary = "내 장바구니 목록 조회", description = "내가 담은 수강 신청 장바구니 목록을 조회합니다.")
  @GetMapping
  public ResponseEntity<List<CartListResponse>> getMyCartList(
      @RequestHeader("X-USER-ID") Long userId
  ) {
    return ResponseEntity.ok(cartQueryService.getMyCartList(userId));
  }
}