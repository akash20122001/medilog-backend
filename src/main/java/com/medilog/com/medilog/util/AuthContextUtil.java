package com.medilog.com.medilog.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Utility class to retrieve authentication context information
 * from the current HTTP request.
 */
@Component
@Slf4j
public class AuthContextUtil {

    /**
     * Retrieves the logged-in user's account ID from the current request context.
     * This assumes that the JWT authentication filter has already processed the request
     * and set the userId attribute.
     *
     * @return The account ID of the currently logged-in user
     * @throws IllegalStateException if no user is logged in or request context is not available
     */
    public Long getLoggedInAccountId() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            
            Long userId = (Long) request.getAttribute("userId");
            
            if (userId == null) {
                log.error("No userId found in request attributes. User might not be authenticated.");
                throw new IllegalStateException("User is not authenticated or userId not found in request context");
            }
            
            log.debug("Retrieved logged-in account ID: {}", userId);
            return userId;
            
        } catch (IllegalStateException e) {
            log.error("Request context not available. This method should be called within a web request.");
            throw new IllegalStateException("Request context not available. Ensure this is called within a web request.", e);
        }
    }

    /**
     * Retrieves the logged-in user's email from the current request context.
     * This assumes that the JWT authentication filter has already processed the request
     * and set the userEmail attribute.
     *
     * @return The email of the currently logged-in user
     * @throws IllegalStateException if no user is logged in or request context is not available
     */
    public String getLoggedInUserEmail() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            
            String userEmail = (String) request.getAttribute("userEmail");
            
            if (userEmail == null) {
                log.error("No userEmail found in request attributes. User might not be authenticated.");
                throw new IllegalStateException("User is not authenticated or userEmail not found in request context");
            }
            
            log.debug("Retrieved logged-in user email: {}", userEmail);
            return userEmail;
            
        } catch (IllegalStateException e) {
            log.error("Request context not available. This method should be called within a web request.");
            throw new IllegalStateException("Request context not available. Ensure this is called within a web request.", e);
        }
    }

    /**
     * Checks if a user is currently authenticated by verifying if userId exists in request context.
     *
     * @return true if user is authenticated, false otherwise
     */
    public boolean isUserAuthenticated() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            
            Long userId = (Long) request.getAttribute("userId");
            return userId != null;
            
        } catch (IllegalStateException e) {
            log.debug("Request context not available, user not authenticated");
            return false;
        }
    }
}
