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
@CrossOrigin(origins = "*")
@Slf4j
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> signup(@Valid @RequestBody SignupRequest request) {
        log.info("Signup request received for email: {}", request.getEmail());
        
        AuthResponse response = authService.signup(request);
        return ResponseEntity.ok(new ApiResponse(true, "User registered successfully", response));
    }
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request received for email: {}", request.getEmail());
        
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(new ApiResponse(true, "Login successful", response));
    }
}