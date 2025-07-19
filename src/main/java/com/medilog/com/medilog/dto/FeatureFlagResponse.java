package com.medilog.com.medilog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeatureFlagResponse {
    
    private Long id;
    private String featureFlagName;
    private List<Long> enabledAccountIds;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}