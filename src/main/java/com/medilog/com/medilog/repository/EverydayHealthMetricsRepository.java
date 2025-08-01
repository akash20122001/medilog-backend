package com.medilog.com.medilog.repository;

import com.medilog.com.medilog.entity.EverydayHealthMetrics;
import com.medilog.com.medilog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface EverydayHealthMetricsRepository extends JpaRepository<EverydayHealthMetrics, Long> {
    
    Optional<EverydayHealthMetrics> findByUserAndDate(User user, LocalDate date);
    
    boolean existsByUserAndDate(User user, LocalDate date);
}