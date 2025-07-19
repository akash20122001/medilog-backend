package com.medilog.com.medilog.controller;

import com.medilog.com.medilog.dto.ApiResponse;
import com.medilog.com.medilog.dto.FeatureFlagRequest;
import com.medilog.com.medilog.dto.FeatureFlagResponse;
import com.medilog.com.medilog.dto.SimpleFeatureFlagResponse;
import com.medilog.com.medilog.exception.FeatureFlagException;
import com.medilog.com.medilog.service.FeatureFlagService;
import com.medilog.com.medilog.util.AuthContextUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/superadmin")
@RequiredArgsConstructor
@Slf4j
public class SuperAdminController {

    private final FeatureFlagService featureFlagService;
    private final AuthContextUtil authContextUtil;

    @PostMapping("/feature-flags")
    public ResponseEntity<ApiResponse> createFeatureFlag(@Valid @RequestBody FeatureFlagRequest request) {
        log.info("Create feature flag request received: {}", request.getFeatureFlagName());

        try {
            FeatureFlagResponse response = featureFlagService.createFeatureFlag(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "Feature flag created successfully", response));
        } catch (FeatureFlagException e) {
            log.error("Error creating feature flag: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PutMapping("/feature-flags/{id}")
    public ResponseEntity<ApiResponse> updateFeatureFlag(
            @PathVariable Long id,
            @Valid @RequestBody FeatureFlagRequest request) {
        log.info("Update feature flag request received for ID: {}", id);

        try {
            FeatureFlagResponse response = featureFlagService.updateFeatureFlag(id, request);
            return ResponseEntity.ok(new ApiResponse(true, "Feature flag updated successfully", response));
        } catch (FeatureFlagException e) {
            log.error("Error updating feature flag: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @DeleteMapping("/feature-flags/{id}")
    public ResponseEntity<ApiResponse> deleteFeatureFlag(@PathVariable Long id) {
        log.info("Delete feature flag request received for ID: {}", id);

        try {
            featureFlagService.deleteFeatureFlag(id);
            return ResponseEntity.ok(new ApiResponse(true, "Feature flag deleted successfully", null));
        } catch (FeatureFlagException e) {
            log.error("Error deleting feature flag: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @GetMapping("/feature-flags")
    public ResponseEntity<ApiResponse> getAllFeatureFlags() {
        log.info("Get all feature flags request received");

        List<FeatureFlagResponse> featureFlags = featureFlagService.getAllFeatureFlags();
        return ResponseEntity.ok(new ApiResponse(true, "Feature flags retrieved successfully", featureFlags));
    }

    @GetMapping("/feature-flags/{id}")
    public ResponseEntity<ApiResponse> getFeatureFlagById(@PathVariable Long id) {
        log.info("Get feature flag by ID request received for ID: {}", id);

        try {
            FeatureFlagResponse response = featureFlagService.getFeatureFlagById(id);
            return ResponseEntity.ok(new ApiResponse(true, "Feature flag retrieved successfully", response));
        } catch (FeatureFlagException e) {
            log.error("Error retrieving feature flag: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @GetMapping("/feature-flags/user")
    public ResponseEntity<ApiResponse> getFeatureFlagsForUser() {
        try {
            Long accountId = authContextUtil.getLoggedInAccountId();
            log.info("Get feature flags for user request received for account ID: {}", accountId);

            List<SimpleFeatureFlagResponse> featureFlags = featureFlagService.getSimpleFeatureFlagsForUser(accountId);
            return ResponseEntity.ok(new ApiResponse(true, "User feature flags retrieved successfully", featureFlags));
        } catch (IllegalStateException e) {
            log.error("Error retrieving logged-in user context: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "User not authenticated", null));
        }
    }

    @GetMapping("/feature-flags/check/{featureFlagName}/user/{accountId}")
    public ResponseEntity<ApiResponse> checkFeatureForUser(
            @PathVariable String featureFlagName,
            @PathVariable Long accountId) {
        log.info("Check feature flag '{}' for account ID: {}", featureFlagName, accountId);

        boolean isEnabled = featureFlagService.isFeatureEnabledForUser(featureFlagName, accountId);
        return ResponseEntity.ok(new ApiResponse(true, "Feature flag status retrieved", isEnabled));
    }
}