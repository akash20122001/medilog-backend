package com.medilog.com.medilog.controller;

import com.medilog.com.medilog.dto.ApiResponse;
import com.medilog.com.medilog.dto.AuthResponse;
import com.medilog.com.medilog.dto.LoginRequest;
import com.medilog.com.medilog.dto.SignupRequest;
import com.medilog.com.medilog.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> signup(@Valid @RequestBody SignupRequest request) {
        log.info("Signup request received for email: {}", request.getEmail());

        AuthResponse response = authService.signup(request);
        return ResponseEntity.ok(new ApiResponse(true, "User registered successfully", response));
    }

    @GetMapping("/hello")
    public ResponseEntity<ApiResponse> hello() {
        log.info("Hello endpoint called");
        return ResponseEntity.ok(new ApiResponse(true, "Hello World", "Hello World"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request received for email: {}", request.getEmail());

        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(new ApiResponse(true, "Login successful", response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout() {
        log.info("Logout request received");

        return ResponseEntity.ok(new ApiResponse(true, "Logout successful", null));
    }
}