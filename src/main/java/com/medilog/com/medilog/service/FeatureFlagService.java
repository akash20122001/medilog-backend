package com.medilog.com.medilog.service;

import com.medilog.com.medilog.dto.FeatureFlagRequest;
import com.medilog.com.medilog.dto.FeatureFlagResponse;
import com.medilog.com.medilog.entity.FeatureFlag;
import com.medilog.com.medilog.exception.FeatureFlagException;
import com.medilog.com.medilog.repository.FeatureFlagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeatureFlagService {
    
    private final FeatureFlagRepository featureFlagRepository;
    
    @Transactional
    public FeatureFlagResponse createFeatureFlag(FeatureFlagRequest request) {
        log.info("Creating feature flag: {}", request.getFeatureFlagName());
        
        if (featureFlagRepository.existsByFeatureFlagName(request.getFeatureFlagName())) {
            throw new FeatureFlagException("Feature flag with name '" + request.getFeatureFlagName() + "' already exists");
        }
        
        FeatureFlag featureFlag = new FeatureFlag();
        featureFlag.setFeatureFlagName(request.getFeatureFlagName());
        featureFlag.setEnabledAccountIds(request.getEnabledAccountIds());
        featureFlag.setDescription(request.getDescription());
        
        FeatureFlag savedFeatureFlag = featureFlagRepository.save(featureFlag);
        log.info("Feature flag created successfully with ID: {}", savedFeatureFlag.getId());
        
        return mapToResponse(savedFeatureFlag);
    }
    
    @Transactional
    public FeatureFlagResponse updateFeatureFlag(Long id, FeatureFlagRequest request) {
        log.info("Updating feature flag with ID: {}", id);
        
        FeatureFlag featureFlag = featureFlagRepository.findById(id)
                .orElseThrow(() -> new FeatureFlagException("Feature flag not found with ID: " + id));
        
        // Check if name is being changed and if new name already exists
        if (!featureFlag.getFeatureFlagName().equals(request.getFeatureFlagName()) &&
            featureFlagRepository.existsByFeatureFlagName(request.getFeatureFlagName())) {
            throw new FeatureFlagException("Feature flag with name '" + request.getFeatureFlagName() + "' already exists");
        }
        
        featureFlag.setFeatureFlagName(request.getFeatureFlagName());
        featureFlag.setEnabledAccountIds(request.getEnabledAccountIds());
        featureFlag.setDescription(request.getDescription());
        
        FeatureFlag updatedFeatureFlag = featureFlagRepository.save(featureFlag);
        log.info("Feature flag updated successfully");
        
        return mapToResponse(updatedFeatureFlag);
    }
    
    @Transactional
    public void deleteFeatureFlag(Long id) {
        log.info("Deleting feature flag with ID: {}", id);
        
        if (!featureFlagRepository.existsById(id)) {
            throw new FeatureFlagException("Feature flag not found with ID: " + id);
        }
        
        featureFlagRepository.deleteById(id);
        log.info("Feature flag deleted successfully");
    }
    
    public List<FeatureFlagResponse> getAllFeatureFlags() {
        log.info("Fetching all feature flags");
        
        List<FeatureFlag> featureFlags = featureFlagRepository.findAll();
        return featureFlags.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public List<FeatureFlagResponse> getFeatureFlagsForUser(Long accountId) {
        log.info("Fetching feature flags for account ID: {}", accountId);
        
        List<FeatureFlag> featureFlags = featureFlagRepository.findByEnabledAccountId(accountId);
        return featureFlags.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public FeatureFlagResponse getFeatureFlagById(Long id) {
        log.info("Fetching feature flag with ID: {}", id);
        
        FeatureFlag featureFlag = featureFlagRepository.findById(id)
                .orElseThrow(() -> new FeatureFlagException("Feature flag not found with ID: " + id));
        
        return mapToResponse(featureFlag);
    }
    
    public boolean isFeatureEnabledForUser(String featureFlagName, Long accountId) {
        log.debug("Checking if feature '{}' is enabled for account ID: {}", featureFlagName, accountId);
        
        return featureFlagRepository.findByFeatureFlagName(featureFlagName)
                .map(featureFlag -> featureFlag.getEnabledAccountIds().contains(accountId))
                .orElse(false);
    }
    
    private FeatureFlagResponse mapToResponse(FeatureFlag featureFlag) {
        return new FeatureFlagResponse(
                featureFlag.getId(),
                featureFlag.getFeatureFlagName(),
                featureFlag.getEnabledAccountIds(),
                featureFlag.getDescription(),
                featureFlag.getCreatedAt(),
                featureFlag.getUpdatedAt()
        );
    }
}