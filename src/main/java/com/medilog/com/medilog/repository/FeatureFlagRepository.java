package com.medilog.com.medilog.repository;

import com.medilog.com.medilog.entity.FeatureFlag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeatureFlagRepository extends JpaRepository<FeatureFlag, Long> {

    Optional<FeatureFlag> findByFeatureFlagName(String featureFlagName);

    @Query("SELECT ff FROM FeatureFlag ff JOIN ff.enabledAccountIds accountId WHERE accountId = :accountId")
    List<FeatureFlag> findByEnabledAccountId(@Param("accountId") Long accountId);

    boolean existsByFeatureFlagName(String featureFlagName);
}