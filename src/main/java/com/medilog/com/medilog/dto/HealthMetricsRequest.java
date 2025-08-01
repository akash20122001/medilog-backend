package com.medilog.com.medilog.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class HealthMetricsRequest {
    private Integer waterIntake;
    private Integer sleepDuration;
    private Integer steps;
    private Integer heartRate;
    private Integer systolicBP;
    private Integer diastolicBP;
    private Double weight;
    private String mood;
    private LocalDate date; // For GET requests to specify which date's data to retrieve
}