package com.medilog.com.medilog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthMetricsResponse {
    private Long id;
    private LocalDate date;
    private Integer waterIntake;
    private Integer sleepDuration;
    private Integer steps;
    private Integer heartRate;
    private Integer systolicBP;
    private Integer diastolicBP;
    private Double weight;
    private String mood;
}