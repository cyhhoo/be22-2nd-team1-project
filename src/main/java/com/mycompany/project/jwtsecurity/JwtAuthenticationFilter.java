package com.mycompany.project.jwtsecurity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.project.common.response.ApiResponse;
import com.mycompany.project.exception.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider tokenProvider;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    // 1. Request Header에서 토큰 추출
    String token = resolveToken(request);

    // 2. 토큰 유효성 검사
    if (StringUtils.hasText(token) && tokenProvider.validateToken(token)) {

      String status = tokenProvider.getStatusFromToken(token);
      String requestURI = request.getRequestURI();

      // INACTIVE면서 계정 활성화 API가 아니라면 차단
      if("INACTIVE".equals(status) && !requestURI.startsWith("/api/auth/activate")){
        sendErrorResponse(response);
        return;
      }

      // 3. 유효하면 인증 정보(Authentication)를 가져와서 SecurityContext에 저장
      Authentication authentication = tokenProvider.getAuthentication(token);
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // 4. 다음 필터로 진행
    filterChain.doFilter(request, response);
  }

  // 헤더에서 "Bearer " 뒷부분의 토큰 값만 꺼내오는 메소드
  private String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }

  private void sendErrorResponse(HttpServletResponse response) throws IOException {
    ApiResponse<?> apiResponse = ApiResponse.failure(ErrorCode.ACCOUNT_INACTIVE.getCode(), ErrorCode.ACCOUNT_INACTIVE.getMessage());

    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    response.setContentType("application/json;charset=UTF-8");
    String json = objectMapper.writeValueAsString(apiResponse);
    response.getWriter().write(json);
  }
}