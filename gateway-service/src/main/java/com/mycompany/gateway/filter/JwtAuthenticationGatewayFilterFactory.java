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

                    // Extract user info from JWT
                    String userId = claims.get("userId") != null ? claims.get("userId").toString() : "";
                    String email = claims.getSubject();
                    String role = claims.get("auth") != null ? claims.get("auth").toString() : "";
                    String status = claims.get("status") != null ? claims.get("status").toString() : "";

                    // Add user info to headers
                    ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                            .header("X-User-Id", userId)
                            .header("X-User-Email", email)
                            .header("X-User-Role", role)
                            .header("X-User-Status", status)
                            .build();

                    return chain.filter(exchange.mutate().request(modifiedRequest).build());
                } catch (Exception e) {
                    // Fail to parse JWT, proceed with original request
                    return chain.filter(exchange);
                }
            }

            // No token or invalid token, proceed with original request
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
