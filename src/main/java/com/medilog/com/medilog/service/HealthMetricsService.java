package com.medilog.com.medilog.service;

import com.medilog.com.medilog.dto.HealthMetricsRequest;
import com.medilog.com.medilog.dto.HealthMetricsResponse;
import com.medilog.com.medilog.entity.EverydayHealthMetrics;
import com.medilog.com.medilog.entity.User;
import com.medilog.com.medilog.exception.UserNotFoundException;
import com.medilog.com.medilog.repository.EverydayHealthMetricsRepository;
import com.medilog.com.medilog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class HealthMetricsService {
    
    private final EverydayHealthMetricsRepository healthMetricsRepository;
    private final UserRepository userRepository;
    
    public Optional<HealthMetricsResponse> getHealthMetricsByDate(Long userId, LocalDate date) {
        log.debug("Fetching health metrics for user ID: {} on date: {}", userId, date);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        
        Optional<EverydayHealthMetrics> metrics = healthMetricsRepository.findByUserAndDate(user, date);
        
        if (metrics.isPresent()) {
            EverydayHealthMetrics healthMetrics = metrics.get();
            return Optional.of(new HealthMetricsResponse(
                    healthMetrics.getId(),
                    healthMetrics.getDate(),
                    healthMetrics.getWaterIntake(),
                    healthMetrics.getSleepDuration(),
                    healthMetrics.getSteps(),
                    healthMetrics.getHeartRate(),
                    healthMetrics.getSystolicBP(),
                    healthMetrics.getDiastolicBP(),
                    healthMetrics.getWeight(),
                    healthMetrics.getMood()
            ));
        }
        
        log.debug("No health metrics found for user ID: {} on date: {}", userId, date);
        return Optional.empty();
    }
    
    @Transactional
    public HealthMetricsResponse saveOrUpdateHealthMetrics(Long userId, HealthMetricsRequest request) {
        log.debug("Saving/updating health metrics for user ID: {} on date: {}", userId, LocalDate.now());
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        
        LocalDate today = LocalDate.now();
        
        // Check if metrics already exist for today
        Optional<EverydayHealthMetrics> existingMetrics = healthMetricsRepository.findByUserAndDate(user, today);
        
        EverydayHealthMetrics healthMetrics;
        
        if (existingMetrics.isPresent()) {
            // Update existing record
            healthMetrics = existingMetrics.get();
            log.debug("Updating existing health metrics record ID: {}", healthMetrics.getId());
        } else {
            // Create new record
            healthMetrics = new EverydayHealthMetrics();
            healthMetrics.setUser(user);
            healthMetrics.setDate(today);
            log.debug("Creating new health metrics record for user ID: {}", userId);
        }
        
        // Update all fields
        healthMetrics.setWaterIntake(request.getWaterIntake());
        healthMetrics.setSleepDuration(request.getSleepDuration());
        healthMetrics.setSteps(request.getSteps());
        healthMetrics.setHeartRate(request.getHeartRate());
        healthMetrics.setSystolicBP(request.getSystolicBP());
        healthMetrics.setDiastolicBP(request.getDiastolicBP());
        healthMetrics.setWeight(request.getWeight());
        healthMetrics.setMood(request.getMood());
        
        EverydayHealthMetrics savedMetrics = healthMetricsRepository.save(healthMetrics);
        
        log.info("Health metrics saved successfully for user ID: {} on date: {}", userId, today);
        
        return new HealthMetricsResponse(
                savedMetrics.getId(),
                savedMetrics.getDate(),
                savedMetrics.getWaterIntake(),
                savedMetrics.getSleepDuration(),
                savedMetrics.getSteps(),
                savedMetrics.getHeartRate(),
                savedMetrics.getSystolicBP(),
                savedMetrics.getDiastolicBP(),
                savedMetrics.getWeight(),
                savedMetrics.getMood()
        );
    }
}