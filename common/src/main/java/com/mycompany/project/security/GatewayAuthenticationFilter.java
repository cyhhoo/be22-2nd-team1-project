package com.mycompany.project.security;

import com.mycompany.project.user.command.domain.aggregate.Role;
import com.mycompany.project.user.command.domain.aggregate.UserStatus;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * API Gateway에서 전달한 헤더(X-User-Id, X-User-Email, X-User-Role)를 읽어
 * SecurityContextHolder에 인증 정보를 설정하는 필터
 */
public class GatewayAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String email = request.getHeader("X-User-Email");
        String userId = request.getHeader("X-User-Id");
        String roleStr = request.getHeader("X-User-Role");
        String statusStr = request.getHeader("X-User-Status");

        if (email != null && !email.isEmpty() && roleStr != null) {
            // "ROLE_STUDENT" 또는 "STUDENT" 모두 처리
            String cleanRole = roleStr.replace("ROLE_", "");
            Role role = Role.valueOf(cleanRole);
            UserStatus status = (statusStr != null) ? UserStatus.valueOf(statusStr) : UserStatus.ACTIVE;

            CustomUserDetails userDetails = CustomUserDetails.builder()
                    .userId(userId != null ? Long.valueOf(userId) : null)
                    .email(email)
                    .role(role)
                    .status(status)
                    .build();

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,
                    null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
