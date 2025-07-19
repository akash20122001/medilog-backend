package com.medilog.com.medilog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeatureFlagRequest {
    
    @NotBlank(message = "Feature flag name is required")
    private String featureFlagName;
    
    @NotNull(message = "Enabled account IDs list is required")
    private List<Long> enabledAccountIds;
    
    private String description;
}