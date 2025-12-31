package com.market.userservice.controller;

import com.market.userservice.dto.AuthRequest;
import com.market.userservice.dto.AuthResponse;
import com.market.userservice.dto.RefreshRequest;
import com.market.userservice.entity.RefreshToken;
import com.market.userservice.entity.User;
import com.market.userservice.repository.UserRepository;
import com.market.userservice.service.JwtService;
import com.market.userservice.service.RefreshTokenService;
import com.market.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final UserRepository repo;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthRequest request) {
        String message = userService.register(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(message); // sends "User registered successfully" to frontend
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(), request.getPassword()
                    )
            );

            User user = (User) auth.getPrincipal();

            // Generate JWT access token
            String accessToken = jwtService.generateToken(user);

            // Create refresh token (transactional)
            String refreshToken = refreshTokenService.create(user.getId()).getToken();

            return ResponseEntity.ok(
                    new AuthResponse(accessToken, refreshToken)
            );

        } catch (AuthenticationException ex) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid credentials");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshRequest request) {
        try {
            RefreshToken refreshToken =
                    refreshTokenService.validate(request.getRefreshToken());

            User user = userService.loadUserById(refreshToken.getUserId());

            String newAccessToken = jwtService.generateToken(user);

            return ResponseEntity.ok(
                    new AuthResponse(newAccessToken, refreshToken.getToken())
            );

        } catch (RuntimeException ex) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ex.getMessage());
        }
    }


    @GetMapping("/me")

    public Mono<Map<String, Object>> me(Authentication authentication) {
        if (authentication == null) {
            return Mono.just(Map.of("error", "Not authenticated"));
        }

        Map<String, Object> userInfo = Map.of(
                "username", authentication.getName(),
                "authorities", authentication.getAuthorities()
        );

        return Mono.just(userInfo);
    }





}
