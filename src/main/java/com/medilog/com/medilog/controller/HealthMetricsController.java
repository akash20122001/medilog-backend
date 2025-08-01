package com.medilog.com.medilog.controller;

import com.medilog.com.medilog.dto.ApiResponse;
import com.medilog.com.medilog.dto.HealthMetricsRequest;
import com.medilog.com.medilog.dto.HealthMetricsResponse;
import com.medilog.com.medilog.service.HealthMetricsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
 @RequestMapping("/api/health-metrics")
@RequiredArgsConstructor
@Slf4j
public class HealthMetricsController {

    private final HealthMetricsService healthMetricsService;

    @PostMapping("/get-by-date")
    public ResponseEntity<ApiResponse> getHealthMetricsByDate(
            @RequestBody HealthMetricsRequest request,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");

        if (userId == null) {
            log.warn("Unauthorized health metrics access attempt");
            return ResponseEntity.status(401)
                    .body(new ApiResponse(false, "Authentication required"));
        }

        if (request.getDate() == null) {
            log.warn("Date not provided in request for user ID: {}", userId);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Date is required"));
        }

        log.debug("Fetching health metrics for user ID: {} on date: {}", userId, request.getDate());

        Optional<HealthMetricsResponse> metrics = healthMetricsService.getHealthMetricsByDate(userId,
                request.getDate());

        if (metrics.isPresent()) {
            return ResponseEntity.ok(new ApiResponse(true, "Health metrics retrieved successfully", metrics.get()));
        } else {
            return ResponseEntity.ok(new ApiResponse(true, "No health metrics found for the specified date", null));
        }
    }

    @PostMapping("/save")
    public ResponseEntity<ApiResponse> saveOrUpdateHealthMetrics(
            @RequestBody HealthMetricsRequest request,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");

        if (userId == null) {
            log.warn("Unauthorized health metrics save attempt");
            return ResponseEntity.status(401)
                    .body(new ApiResponse(false, "Authentication required"));
        }

        log.debug("Saving health metrics for user ID: {}", userId);

        try {
            HealthMetricsResponse savedMetrics = healthMetricsService.saveOrUpdateHealthMetrics(userId, request);
            return ResponseEntity.ok(new ApiResponse(true, "Health metrics saved successfully", savedMetrics));
        } catch (Exception e) {
            log.error("Error saving health metrics for user ID: {}", userId, e);
            return ResponseEntity.status(500)
                    .body(new ApiResponse(false, "Failed to save health metrics: " + e.getMessage()));
        }
    }
}