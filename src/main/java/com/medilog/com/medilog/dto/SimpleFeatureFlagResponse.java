package com.medilog.com.medilog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleFeatureFlagResponse {

    private Long id;
    private String featureFlagName;
}