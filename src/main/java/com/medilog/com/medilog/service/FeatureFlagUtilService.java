package com.medilog.com.medilog.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Utility service for feature flag operations that can be used throughout the application
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FeatureFlagUtilService {
    
    private final FeatureFlagService featureFlagService;
    
    /**
     * Check if a feature is enabled for a specific user
     * @param featureFlagName the name of the feature flag
     * @param accountId the account ID to check
     * @return true if the feature is enabled for the user, false otherwise
     */
    public boolean isFeatureEnabled(String featureFlagName, Long accountId) {
        return featureFlagService.isFeatureEnabledForUser(featureFlagName, accountId);
    }
    
    /**
     * Execute code block only if feature is enabled for user
     * @param featureFlagName the name of the feature flag
     * @param accountId the account ID to check
     * @param action the action to execute if feature is enabled
     */
    public void executeIfEnabled(String featureFlagName, Long accountId, Runnable action) {
        if (isFeatureEnabled(featureFlagName, accountId)) {
            log.debug("Executing action for enabled feature: {} for account: {}", featureFlagName, accountId);
            action.run();
        } else {
            log.debug("Feature {} is not enabled for account: {}, skipping action", featureFlagName, accountId);
        }
    }
}