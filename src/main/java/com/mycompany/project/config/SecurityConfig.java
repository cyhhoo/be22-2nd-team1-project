package com.mycompany.project.config;

import com.mycompany.project.jwtsecurity.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtTokenProvider jwtTokenProvider;
  private final RestAuthenticationEntryPoint authenticationEntryPoint;
  private final RestAccessDeniedHandler accessDeniedHandler;
  private final JwtExceptionFilter jwtExceptionFilter;
  private final ObjectMapper objectMapper;

  // 비밀번호 암호화 도구 (BCrypt) 빈 등록
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        // 1. CSRF 비활성화 (Rest API이므로 불필요)
        .csrf(AbstractHttpConfigurer::disable)

        // 2. 세션 미사용 (Stateless 설정)
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

        // 3. URL별 접근 권한 설정
        .authorizeHttpRequests(auth -> auth
            // Swagger UI는 누구나 접근 가능
            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
            // 로그인/회원가입,활성화 API는 누구나 접근 가능
            .requestMatchers("/api/v1/auth/**", "/api/v1/auth/activate").permitAll()
            // 업로드된 파일 접근 허용
            .requestMatchers("/uploads/**").permitAll()
            // 그 외 모든 요청은 인증 필요
            .anyRequest().authenticated())

        // 예외 핸들러 추가
        .exceptionHandling(exception -> exception
            .authenticationEntryPoint(authenticationEntryPoint)
            .accessDeniedHandler(accessDeniedHandler))

        // 4. JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 끼워넣기
        .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, objectMapper),
            UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(jwtExceptionFilter, JwtAuthenticationFilter.class);

    return http.build();
  }
}