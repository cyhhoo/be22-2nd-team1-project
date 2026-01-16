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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Auth Query", description = "Login/Logout and token reissue API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthQueryController {

  private final AuthQueryService authQueryService;

  @Operation(summary = "Login", description = "Login with email and password to receive Access Token and Refresh Token.")
  @PostMapping("/login")
  @PreAuthorize("permitAll()")
  public ResponseEntity<ApiResponse<TokenResponse>> login(@RequestBody LoginRequest request) {
    TokenResponse response = authQueryService.login(request);
    return buildTokenResponse(response);
  }

  @Operation(summary = "Token reissue", description = "Use Refresh Token to get a new Access Token.")
  @PostMapping("/reissue")
  @PreAuthorize("permitAll()")
  public ResponseEntity<ApiResponse<TokenResponse>> reissue(
      @CookieValue(value = "RefreshToken", required = true) String refreshToken) {
    TokenResponse response = authQueryService.reissue(refreshToken);
    return buildTokenResponse(response);
  }

  @Operation(summary = "Logout", description = "Delete Refresh Token and process logout.")
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

  /* Return accessToken and refreshToken in body and cookies */
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
        .maxAge(java.util.Objects.requireNonNull(java.time.Duration.ofMinutes(30)))
        .sameSite("Lax")
        .build();
  }

  private ResponseCookie createRefreshTokenCookie(String refreshToken) {
    return ResponseCookie.from("RefreshToken", refreshToken)
        .httpOnly(true)
        .path("/")
        .maxAge(java.util.Objects.requireNonNull(java.time.Duration.ofDays(7)))
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
