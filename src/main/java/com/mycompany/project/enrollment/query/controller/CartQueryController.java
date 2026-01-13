package com.mycompany.project.enrollment.query.controller;

import com.mycompany.project.common.response.ApiResponse;
import com.mycompany.project.enrollment.query.dto.CartListResponse;
import com.mycompany.project.enrollment.query.service.CartQueryService;
import com.mycompany.project.exception.BusinessException;
import com.mycompany.project.exception.ErrorCode;
import com.mycompany.project.user.command.domain.aggregate.User;
import com.mycompany.project.user.command.domain.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "장바구니 (Cart)", description = "장바구니 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/carts")
public class CartQueryController {

  private final CartQueryService cartQueryService;
  private final UserRepository userRepository;

  @Operation(summary = "내 장바구니 목록 조회", description = "내가 담은 수강 신청 장바구니 목록을 조회합니다.")
  @GetMapping
  public ResponseEntity<ApiResponse<List<CartListResponse>>> getMyCartList() {
    Long userId = getCurrentUserId();
    return ResponseEntity.ok(ApiResponse.success(cartQueryService.getMyCartList(userId)));
  }

  private Long getCurrentUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || authentication.getName() == null) {
      throw new BusinessException(ErrorCode.LOGIN_REQUIRED);
    }

    String email = authentication.getName();

    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

    return user.getUserId();
  }
}
