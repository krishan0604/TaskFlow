package com.taskapp.apigateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Predicate;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Value("${jwt.secret}")
    private String jwtSecret;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            final List<String> openEndpoints = List.of("/auth/register", "/auth/login", "/auth/refresh", "/eureka");

            Predicate<ServerHttpRequest> isSecured = r -> openEndpoints.stream()
                    .noneMatch(uri -> r.getURI().getPath().contains(uri));

            if (isSecured.test(request)) {
                if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    return onError(exchange, "Missing authorization header", HttpStatus.UNAUTHORIZED);
                }

                String authHeader = request.getHeaders().getOrEmpty(HttpHeaders.AUTHORIZATION).get(0);
                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    return onError(exchange, "Invalid authorization header format", HttpStatus.UNAUTHORIZED);
                }

                String token = authHeader.substring(7);

                try {
                    Claims claims = Jwts.parser()
                            .verifyWith(getSignInKey())
                            .build()
                            .parseSignedClaims(token)
                            .getPayload();

                    String userId = claims.get("userId", String.class);
                    String role = claims.get("roles", String.class);

                    request = exchange.getRequest().mutate()
                            .header("X-User-Id", userId != null ? userId : "")
                            .header("X-User-Role", role != null ? role : "")
                            .build();

                } catch (Exception e) {
                    return onError(exchange, "JWT Token validation failed: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
                }
            }

            return chain.filter(exchange.mutate().request(request).build());
        };
    }

    // KEY FIX: try Base64 first, fall back to plain UTF-8 bytes
    private SecretKey getSignInKey() {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        }
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }

    public static class Config {
    }
}