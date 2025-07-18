package com.medilog.com.medilog.service;

import com.medilog.com.medilog.dto.AuthResponse;
import com.medilog.com.medilog.dto.LoginRequest;
import com.medilog.com.medilog.dto.SignupRequest;
import com.medilog.com.medilog.entity.User;
import com.medilog.com.medilog.exception.EmailAlreadyExistsException;
import com.medilog.com.medilog.exception.InvalidCredentialsException;
import com.medilog.com.medilog.repository.UserRepository;
import com.medilog.com.medilog.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;
    
    @Transactional
    public AuthResponse signup(SignupRequest request) {
        log.info("Attempting to register user with email: {}", request.getEmail());
        
        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed - email already exists: {}", request.getEmail());
            throw new EmailAlreadyExistsException("An account with this email already exists");
        }
        
        // Create new user
        User user = new User();
        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        user.setEmail(request.getEmail().toLowerCase().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());
        
        // Generate JWT token
        String token = jwtUtil.generateToken(savedUser.getEmail(), savedUser.getId());
        
        return new AuthResponse(token, savedUser.getId(), savedUser.getFirstName(), 
                               savedUser.getLastName(), savedUser.getEmail());
    }
    
    public AuthResponse login(LoginRequest request) {
        log.info("Attempting login for email: {}", request.getEmail());
        
        // Find user by email
        User user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> {
                    log.warn("Login failed - user not found: {}", request.getEmail());
                    return new InvalidCredentialsException("Invalid email or password");
                });
        
        // Check password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Login failed - invalid password for user: {}", request.getEmail());
            throw new InvalidCredentialsException("Invalid email or password");
        }
        
        log.info("User logged in successfully: {}", user.getEmail());
        
        // Generate JWT token
        String token = jwtUtil.generateToken(user.getEmail(), user.getId());
        
        return new AuthResponse(token, user.getId(), user.getFirstName(), 
                               user.getLastName(), user.getEmail());
    }
}