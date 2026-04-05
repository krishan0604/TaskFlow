package com.taskapp.authservice.service;

import com.taskapp.authservice.dto.LoginRequest;
import com.taskapp.authservice.dto.RefreshTokenRequest;
import com.taskapp.authservice.dto.RegisterRequest;
import com.taskapp.authservice.dto.TokenResponse;
import com.taskapp.authservice.entity.RefreshToken;
import com.taskapp.authservice.entity.User;
import com.taskapp.authservice.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public User register(RegisterRequest request) {
        return userService.register(request);
    }

    public TokenResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        var user = userService.findByUsername(request.username());
        return generateTokens(user);
    }

    public TokenResponse refresh(RefreshTokenRequest request) {
        RefreshToken token = refreshTokenService.findByToken(request.refreshToken());
        refreshTokenService.verifyExpiration(token);
        return generateTokens(token.getUser());
    }

    public void logout(RefreshTokenRequest request) {
        refreshTokenService.revokeToken(request.refreshToken());
    }

    private TokenResponse generateTokens(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId().toString());
        claims.put("roles", user.getRole().name());

        String accessToken = jwtService.generateToken(claims, user.getUsername());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return new TokenResponse(
                accessToken,
                refreshToken.getToken(),
                "Bearer",
                jwtExpiration / 1000 // in seconds
        );
    }
}
