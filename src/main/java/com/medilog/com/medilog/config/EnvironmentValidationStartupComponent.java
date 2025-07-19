package com.medilog.com.medilog.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.medilog.com.medilog.exception.EnvironmentConfigurationException;

import jakarta.annotation.PostConstruct;

/**
 * Startup component that validates environment configuration and provides
 * fail-fast behavior for misconfigured environments.
 * 
 * This component ensures that:
 * - Environment configuration is valid
 * - Database environment separation is properly configured
 * - Clear logging of active environment and table prefix information
 */
@Component
public class EnvironmentValidationStartupComponent implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(EnvironmentValidationStartupComponent.class);

    private final DatabaseEnvironmentProperties databaseProperties;
    private final Environment environment;
    private final EnvironmentAwarePhysicalNamingStrategy namingStrategy;

    @Autowired
    public EnvironmentValidationStartupComponent(
            DatabaseEnvironmentProperties databaseProperties,
            Environment environment,
            EnvironmentAwarePhysicalNamingStrategy namingStrategy) {
        this.databaseProperties = databaseProperties;
        this.environment = environment;
        this.namingStrategy = namingStrategy;
    }

    @PostConstruct
    public void validateConfiguration() {
        logger.info("Starting environment configuration validation...");
        
        // Validate database environment properties
        if (databaseProperties.isValidateOnStartup()) {
            validateDatabaseEnvironmentConfiguration();
        }
        
        // Log environment information
        logEnvironmentInformation();
        
        logger.info("Environment configuration validation completed successfully");
    }

    @Override
    public void run(String... args) throws Exception {
        // Additional validation that runs after full application context is loaded
        validateRuntimeConfiguration();
    }

    /**
     * Validates the database environment configuration
     * Implements fail-fast behavior for invalid configurations
     */
    private void validateDatabaseEnvironmentConfiguration() {
        logger.debug("Validating database environment configuration...");
        
        // Check if environment separation is enabled
        if (!databaseProperties.isEnableEnvironmentSeparation()) {
            logger.warn("Environment separation is DISABLED - all environments will share the same tables");
            return;
        }
        
        // Validate environment configuration
        if (!databaseProperties.isValidConfiguration()) {
            String errorMessage = String.format(
                "Invalid database environment configuration. " +
                "Environment: '%s', Expected: one of [dev, prod, test]. " +
                "Current configuration: %s",
                databaseProperties.getEnvironment(),
                databaseProperties.toString()
            );
            logger.error(errorMessage);
            throw new EnvironmentConfigurationException(
                errorMessage, 
                databaseProperties.getEnvironment(), 
                "Invalid environment value"
            );
        }
        
        // Validate that environment matches active Spring profile
        String[] activeProfiles = environment.getActiveProfiles();
        String configuredEnvironment = databaseProperties.getEnvironment();
        
        if (activeProfiles.length == 0) {
            logger.warn("No active Spring profiles detected, using default profile");
        } else {
            boolean profileMatches = false;
            for (String profile : activeProfiles) {
                if (profile.equals(configuredEnvironment)) {
                    profileMatches = true;
                    break;
                }
            }
            
            if (!profileMatches) {
                String errorMessage = String.format(
                    "Database environment configuration mismatch. " +
                    "Configured environment: '%s', Active profiles: %s. " +
                    "Environment should match one of the active profiles.",
                    configuredEnvironment,
                    String.join(", ", activeProfiles)
                );
                logger.error(errorMessage);
                throw new EnvironmentConfigurationException(
                    errorMessage, 
                    configuredEnvironment, 
                    "Environment-profile mismatch"
                );
            }
        }
        
        // Validate table prefix
        String tablePrefix = databaseProperties.getComputedTablePrefix();
        if (tablePrefix == null || tablePrefix.trim().isEmpty()) {
            String errorMessage = "Table prefix cannot be null or empty when environment separation is enabled";
            logger.error(errorMessage);
            throw new EnvironmentConfigurationException(
                errorMessage, 
                databaseProperties.getEnvironment(), 
                "Empty table prefix"
            );
        }
        
        logger.debug("Database environment configuration validation passed");
    }

    /**
     * Logs comprehensive environment information for debugging and monitoring
     */
    private void logEnvironmentInformation() {
        logger.info("=== MEDILOG ENVIRONMENT INFORMATION ===");
        
        // Active Spring profiles
        String[] activeProfiles = environment.getActiveProfiles();
        String[] defaultProfiles = environment.getDefaultProfiles();
        
        logger.info("Active Spring Profiles: {}", 
            activeProfiles != null && activeProfiles.length > 0 ? String.join(", ", activeProfiles) : "None");
        logger.info("Default Spring Profiles: {}", 
            defaultProfiles != null && defaultProfiles.length > 0 ? String.join(", ", defaultProfiles) : "None");
        
        // Database environment configuration
        logger.info("Database Environment: {}", databaseProperties.getEnvironment());
        logger.info("Environment Separation Enabled: {}", databaseProperties.isEnableEnvironmentSeparation());
        logger.info("Table Prefix: '{}'", databaseProperties.getComputedTablePrefix());
        logger.info("Validation on Startup: {}", databaseProperties.isValidateOnStartup());
        
        // Database connection information (without sensitive data)
        String datasourceUrl = environment.getProperty("spring.datasource.url", "Not configured");
        String datasourceUsername = environment.getProperty("spring.datasource.username", "Not configured");
        logger.info("Database URL: {}", maskSensitiveUrl(datasourceUrl));
        logger.info("Database Username: {}", maskUsername(datasourceUsername));
        
        // JPA/Hibernate configuration
        String ddlAuto = environment.getProperty("spring.jpa.hibernate.ddl-auto", "Not configured");
        String dialect = environment.getProperty("spring.jpa.properties.hibernate.dialect", "Not configured");
        logger.info("Hibernate DDL Auto: {}", ddlAuto);
        logger.info("Hibernate Dialect: {}", dialect);
        
        logger.info("=== END ENVIRONMENT INFORMATION ===");
    }

    /**
     * Validates runtime configuration after application context is fully loaded
     */
    private void validateRuntimeConfiguration() {
        logger.debug("Performing runtime configuration validation...");
        
        // Validate that naming strategy is working correctly
        try {
            // This will trigger the naming strategy to compute table prefix
            String computedPrefix = databaseProperties.getComputedTablePrefix();
            logger.info("Runtime validation: Table prefix computation successful: '{}'", computedPrefix);
        } catch (Exception e) {
            String errorMessage = "Runtime validation failed: Unable to compute table prefix";
            logger.error(errorMessage, e);
            throw new EnvironmentConfigurationException(
                errorMessage, 
                databaseProperties.getEnvironment(), 
                "Runtime validation failure", 
                e
            );
        }
        
        // Additional runtime checks can be added here
        logger.debug("Runtime configuration validation completed");
    }

    /**
     * Masks sensitive information in database URL for logging
     */
    private String maskSensitiveUrl(String url) {
        if (url == null || url.equals("Not configured")) {
            return url;
        }
        
        // Mask password in URL if present
        return url.replaceAll("password=[^&;]*", "password=***");
    }

    /**
     * Masks username for logging (shows first and last character)
     */
    private String maskUsername(String username) {
        if (username == null || username.equals("Not configured") || username.length() <= 2) {
            return username;
        }
        
        return username.charAt(0) + "***" + username.charAt(username.length() - 1);
    }
}