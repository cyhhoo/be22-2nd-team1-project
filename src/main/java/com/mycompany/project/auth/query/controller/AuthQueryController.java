package com.mycompany.project.auth.query.controller;

import com.mycompany.project.auth.query.dto.LoginRequest;
import com.mycompany.project.auth.query.dto.TokenResponse;
import com.mycompany.project.auth.query.service.AuthQueryService; // Refactored dependency
import com.mycompany.project.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthQueryController {

  private final AuthQueryService authQueryService;

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<TokenResponse>> login(@RequestBody LoginRequest request) {
    TokenResponse response = authQueryService.login(request);
    return buildTokenResponse(response);
  }

  @PostMapping("/reissue")
  public ResponseEntity<ApiResponse<TokenResponse>> reissue(
      @CookieValue(value = "RefreshToken", required = true) String refreshToken) {
    TokenResponse response = authQueryService.reissue(refreshToken);
    return buildTokenResponse(response);
  }

  @PostMapping("/logout")
  public ResponseEntity<ApiResponse<Void>> logout(
      @CookieValue(value = "RefreshToken", required = false) String refreshToken) {

    if (refreshToken != null) {
      authQueryService.logout(refreshToken);
    }

    ResponseCookie deleteCookie = createDeleteRefreshTokenCookie();

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
        .body(ApiResponse.success(null));
  }

  /* accessToken 과 refreshToken을 body와 쿠키에 담아 반환 */
  private ResponseEntity<ApiResponse<TokenResponse>> buildTokenResponse(TokenResponse tokenResponse) {
    ResponseCookie cookie = createRefreshTokenCookie(tokenResponse.getRefreshToken()); // refreshToken 쿠키 생성
    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, cookie.toString())
        .body(ApiResponse.success(tokenResponse));
  }

  /* refreshToken 쿠키 삭제용 설정 */
  private ResponseCookie createRefreshTokenCookie(String refreshToken) {
    return ResponseCookie.from("RefreshToken", refreshToken)
        .httpOnly(true) // HttpOnly 속성 설정 (JavaScript 에서 접근 불가)
        // .secure(true) // HTTPS 환경일 때만 전송 (운영 환경에서 활성화 권장)
        .path("/") // 쿠키 범위 : 전체 경로
        .maxAge(Duration.ofDays(7)) // 쿠키 만료 기간 : 7일
        .sameSite("Strict") // CSRF 공격 방어를 위한 SameSite 설정
        .build();
  }

  private ResponseCookie createDeleteRefreshTokenCookie() {
    return ResponseCookie.from("RefreshToken", "")
        .httpOnly(true) // HttpOnly 속성 설정 (JavaScript 에서 접근 불가)
        // .secure(true) // HTTPS 환경일 때만 전송 (운영 환경에서 활성화 권장)
        .path("/") // 쿠키 범위 : 전체 경로
        .maxAge(0) // 쿠키 만료 기간 : 0 ->
        .sameSite("Strict") // CSRF 공격 방어를 위한 SameSite 설정
        .build();
  }

}
