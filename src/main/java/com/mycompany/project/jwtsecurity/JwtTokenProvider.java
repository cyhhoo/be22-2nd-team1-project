package com.mycompany.project.jwtsecurity;

import com.mycompany.project.user.command.domain.aggregate.Role;
import com.mycompany.project.user.command.domain.aggregate.UserStatus;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

  @Value("${jwt.secret}")
  private String secretKey;
  private SecretKey key;

  // 30분, 7일
  private final long ACCESS_TOKEN_VALIDITY = 30 * 60 * 1000L;
  private final long REFRESH_TOKEN_VALIDITY = 7 * 24 * 60 * 60 * 1000L;

  @PostConstruct
  protected void init() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    // 키 길이가 짧으면 에러가 날 수 있으니 application.yaml의 secret을 길게 설정하세요.
    // 만약 에러나면: Keys.hmacShaKeyFor(secretKey.getBytes()) 로 변경 가능
    this.key = Keys.hmacShaKeyFor(keyBytes);
  }

  // 토큰 생성
  public String createAccessToken(String email, Role role, UserStatus status) {
    return createToken(email, role, status, ACCESS_TOKEN_VALIDITY);
  }

  public String createRefreshToken(String email) {
    return createToken(email, null , null , REFRESH_TOKEN_VALIDITY);
  }

  private String createToken(String email, Role role, UserStatus status, long validity) {
    Date now = new Date();
    Date validityDate = new Date(now.getTime() + validity);

    JwtBuilder builder = Jwts.builder()
        .subject(email)
        .issuedAt(now)
        .expiration(validityDate)
        .signWith(key);

    if (role != null) {
      builder.claim("auth", role.name());
    }

    if (status != null){
      builder.claim("status",status.name());
    }

    return builder.compact();
  }

  // 토큰에서 인증 정보 조회
  public Authentication getAuthentication(String token) {
    Claims claims = parseClaims(token);

    // 권한 정보가 없으면 빈 권한 목록
    Collection<? extends GrantedAuthority> authorities = Collections.emptyList();

    if (claims.get("auth") != null) {
      authorities = Arrays.stream(claims.get("auth").toString().split(","))
          .map(SimpleGrantedAuthority::new)
          .collect(Collectors.toList());
    }

    UserDetails principal = new User(claims.getSubject(), "", authorities);
    return new UsernamePasswordAuthenticationToken(principal, "", authorities);
  }

  // 토큰 유효성 검증
  public boolean validateToken(String token) {
    try {
      parseClaims(token);
      return true;
    } catch (Exception e) {
      // ExpiredJwtException, UnsupportedJwtException, MalformedJwtException etc.
      return false;
    }
  }

  private Claims parseClaims(String token) {
    return Jwts.parser()
        .verifyWith(key)
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  public String getUserEmailFromJWT(String refreshToken) {
    Claims claims = Jwts.parser()
        .verifyWith(key)
        .build()
        .parseSignedClaims(refreshToken)
        .getPayload();
    return claims.getSubject();
  }

  public String getStatusFromToken(String token){
    try {
      Claims claims = parseClaims(token);
      return claims.get("status", String.class);
    }
    catch (Exception e){
      return null;
    }
  }
}