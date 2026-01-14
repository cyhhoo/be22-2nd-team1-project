package com.mycompany.gateway.filter;

import com.mycompany.project.security.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationGatewayFilterFactory
        extends AbstractGatewayFilterFactory<JwtAuthenticationGatewayFilterFactory.Config> {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationGatewayFilterFactory(JwtTokenProvider jwtTokenProvider) {
        super(Config.class);
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String token = extractToken(exchange.getRequest());

            if (token != null && jwtTokenProvider.validateToken(token)) {
                try {
                    Claims claims = jwtTokenProvider.parseClaims(token);

                    // JWT에서 사용자 정보 추출
                    String userId = claims.get("userId") != null ? claims.get("userId").toString() : "";
                    String email = claims.getSubject();
                    String role = claims.get("auth") != null ? claims.get("auth").toString() : "";
                    String status = claims.get("status") != null ? claims.get("status").toString() : "";

                    // 헤더에 사용자 정보 추가
                    ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                            .header("X-User-Id", userId)
                            .header("X-User-Email", email)
                            .header("X-User-Role", role)
                            .header("X-User-Status", status)
                            .build();

                    return chain.filter(exchange.mutate().request(modifiedRequest).build());
                } catch (Exception e) {
                    // JWT 파싱 실패 시 원본 요청 그대로 전달
                    return chain.filter(exchange);
                }
            }

            // 토큰이 없거나 유효하지 않으면 원본 요청 그대로 전달
            return chain.filter(exchange);
        };
    }

    private String extractToken(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public static class Config {
        // Configuration properties if needed
    }
}
