package com.mycompany.project.common.config;

import com.mycompany.project.jwtsecurity.JwtAuthenticationFilter;
import com.mycompany.project.jwtsecurity.JwtTokenProvider;
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
            // 로그인/회원가입 API는 누구나 접근 가능
            .requestMatchers("/api/auth/**").permitAll()
            // 업로드된 파일 접근 허용
            .requestMatchers("/uploads/**").permitAll()
            // 그 외 모든 요청은 인증 필요
            .anyRequest().authenticated())

        // 4. JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 끼워넣기
        .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}