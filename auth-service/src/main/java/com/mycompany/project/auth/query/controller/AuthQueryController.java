package com.mycompany.project.auth.query.controller;

import com.mycompany.project.auth.query.dto.LoginRequest;
import com.mycompany.project.auth.query.dto.TokenResponse;
import com.mycompany.project.auth.query.service.AuthQueryService;
import com.mycompany.project.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "인증 조회 (Auth Query)", description = "로그인/로그아웃/토큰 재발급 API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthQueryController {

  private final AuthQueryService authQueryService;

  @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하여 Access Token과 Refresh Token을 발급받습니다.")
  @PostMapping("/login")
  @PreAuthorize("permitAll()")
  public ResponseEntity<ApiResponse<TokenResponse>> login(@RequestBody LoginRequest request) {
    TokenResponse response = authQueryService.login(request);
    return buildTokenResponse(response);
  }

  @Operation(summary = "토큰 재발급", description = "Refresh Token을 사용하여 새로운 Access Token을 발급받습니다.")
  @PostMapping("/reissue")
  @PreAuthorize("permitAll()")
  public ResponseEntity<ApiResponse<TokenResponse>> reissue(
      @CookieValue(value = "RefreshToken", required = true) String refreshToken) {
    TokenResponse response = authQueryService.reissue(refreshToken);
    return buildTokenResponse(response);
  }

  @Operation(summary = "로그아웃", description = "Refresh Token을 삭제하고 로그아웃 처리합니다.")
  @PostMapping("/logout")
  @PreAuthorize("permitAll()")
  public ResponseEntity<ApiResponse<Void>> logout(
      @CookieValue(value = "RefreshToken", required = false) String refreshToken) {

    if (refreshToken != null) {
      authQueryService.logout(refreshToken);
    }

    ResponseCookie deleteAccessCookie = createDeleteAccessTokenCookie();
    ResponseCookie deleteRefreshCookie = createDeleteRefreshTokenCookie();

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.SET_COOKIE, deleteAccessCookie.toString());
    headers.add(HttpHeaders.SET_COOKIE, deleteRefreshCookie.toString());

    return new ResponseEntity<>(ApiResponse.success(null), headers, org.springframework.http.HttpStatus.OK);
  }

  /* accessToken 과 refreshToken을 body와 쿠키에 담아 반환 */
  private ResponseEntity<ApiResponse<TokenResponse>> buildTokenResponse(TokenResponse tokenResponse) {
    ResponseCookie accessTokenCookie = createAccessTokenCookie(tokenResponse.getAccessToken());
    ResponseCookie refreshTokenCookie = createRefreshTokenCookie(tokenResponse.getRefreshToken());

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
    headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

    return new ResponseEntity<>(ApiResponse.success(tokenResponse), headers, org.springframework.http.HttpStatus.OK);
  }

  private ResponseCookie createAccessTokenCookie(String accessToken) {
    return ResponseCookie.from("AccessToken", accessToken)
        .httpOnly(true)
        .path("/")
        .maxAge(Duration.ofMinutes(30))
        .sameSite("Lax") // 호환성을 위해 Lax 사용
        .build();
  }

  private ResponseCookie createRefreshTokenCookie(String refreshToken) {
    return ResponseCookie.from("RefreshToken", refreshToken)
        .httpOnly(true)
        .path("/")
        .maxAge(Duration.ofDays(7))
        .sameSite("Lax")
        .build();
  }

  private ResponseCookie createDeleteAccessTokenCookie() {
    return ResponseCookie.from("AccessToken", "")
        .httpOnly(true)
        .path("/")
        .maxAge(0)
        .sameSite("Lax")
        .build();
  }

  private ResponseCookie createDeleteRefreshTokenCookie() {
    return ResponseCookie.from("RefreshToken", "")
        .httpOnly(true)
        .path("/")
        .maxAge(0)
        .sameSite("Lax")
        .build();
  }
}
