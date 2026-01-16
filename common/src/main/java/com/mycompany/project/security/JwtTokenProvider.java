package com.mycompany.project.security;

import com.mycompany.project.common.enums.Role;
import com.mycompany.project.common.enums.UserStatus;
import io.jsonwebtoken.*;
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

    // 30遺? 7??
    private final long ACCESS_TOKEN_VALIDITY = 30 * 60 * 1000L;
    private final long REFRESH_TOKEN_VALIDITY = 7 * 24 * 60 * 60 * 1000L;

    @PostConstruct
    protected void init() {
        // Base64 ?붿퐫?????吏곸젒 諛붿씠?몃줈 蹂??(plain text secret 吏??
        byte[] keyBytes = secretKey.getBytes();
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // ?좏겙 ?앹꽦
    public String createAccessToken(Long userId, String email, Role role, UserStatus status) {
        Date now = new Date();
        Date validityDate = new Date(now.getTime() + ACCESS_TOKEN_VALIDITY);

        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)
                .claim("auth", role.name())
                .claim("status", status.name())
                .setIssuedAt(now)
                .setExpiration(validityDate)
                .signWith(key)
                .compact();

    }

    public String createRefreshToken(String email) {
        return createToken(email, null, null, REFRESH_TOKEN_VALIDITY);
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

        if (status != null) {
            builder.claim("status", status.name());
        }

        return builder.compact();
    }

    // ?좏겙?먯꽌 ?몄쬆 ?뺣낫 議고쉶
    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);

        Object userIdObj = claims.get("userId");
        Object authObj = claims.get("auth");
        Object statusObj = claims.get("status");

        // refresh token ??寃쎌슦, ?대떦 ?대젅?꾩씠 null?대씪???덉쟾?섍쾶 泥섎━??
        CustomUserDetails userdetails = CustomUserDetails.builder()
                .userId(userIdObj != null ? Long.valueOf(userIdObj.toString()) : null)
                .email(claims.getSubject())
                .role(authObj != null ? Role.valueOf(authObj.toString()) : null)
                .status(statusObj != null ? UserStatus.valueOf(statusObj.toString()) : null)
                .build();

        // 沅뚰븳 紐⑸줉 媛?몄삤湲? role??null??寃쎌슦 鍮?由ъ뒪??諛섑솚?댁꽌 getAuthorities() ?몄텧 ?덈맖?쇰줈 NPE ?먮윭 諛⑹?
        Collection<? extends GrantedAuthority> authorities = (userdetails.getRole() != null)
                ? userdetails.getAuthorities()
                : Collections.emptyList();

        return new UsernamePasswordAuthenticationToken(userdetails, "", authorities);
    }

    // ?좏겙 ?좏슚??寃利?
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception e) {
            // ExpiredJwtException, UnsupportedJwtException, MalformedJwtException etc.
            return false;
        }
    }

    public Claims parseClaims(String token) {
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

    public String getStatusFromToken(String token) {
        try {
            Claims claims = parseClaims(token);
            return claims.get("status", String.class);
        } catch (Exception e) {
            return null;
        }
    }
}
