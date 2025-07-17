package com.medilog.com.medilog.controller;

import com.medilog.com.medilog.dto.ApiResponse;
import com.medilog.com.medilog.entity.User;
import com.medilog.com.medilog.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {
    
    private final UserService userService;
    
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse> getUserProfile(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String userEmail = (String) request.getAttribute("userEmail");
        
        if (userId == null) {
            return ResponseEntity.status(401)
                .body(new ApiResponse(false, "Authentication required"));
        }
        
        try {
            User user = userService.getUserById(userId);
            return ResponseEntity.ok(new ApiResponse(true, "Profile retrieved successfully", user));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage()));
        }
    }
}