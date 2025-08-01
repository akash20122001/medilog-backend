package com.medilog.com.medilog.controller;

import com.medilog.com.medilog.dto.ApiResponse;
import com.medilog.com.medilog.entity.User;
import com.medilog.com.medilog.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    
    private final UserService userService;
    
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse> getUserProfile(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String userEmail = (String) request.getAttribute("userEmail");
        
        log.debug("Profile request for user ID: {}, email: {}", userId, userEmail);
        
        if (userId == null) {
            log.warn("Unauthorized profile access attempt");
            return ResponseEntity.status(401)
                .body(new ApiResponse(false, "Authentication required"));
        }
        
        User user = userService.getUserById(userId);
        
        // Remove password from response for security
        user.setPassword(null);
        
        return ResponseEntity.ok(new ApiResponse(true, "Profile retrieved successfully", user));
    }
}