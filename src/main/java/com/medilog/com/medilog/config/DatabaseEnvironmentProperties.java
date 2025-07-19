package com.medilog.com.medilog.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Configuration properties for database environment-specific settings.
 * Handles environment detection and table prefix customization for data isolation.
 */
@Component
@ConfigurationProperties(prefix = "app.database")
@Validated
public class DatabaseEnvironmentProperties {

    /**
     * The active environment (dev, prod, test)
     */
    @NotBlank(message = "Database environment must be specified")
    @Pattern(regexp = "^(dev|prod|test)$", message = "Environment must be one of: dev, prod, test")
    private String environment;

    /**
     * Table prefix pattern for environment separation
     * Default format: {environment}_
     */
    private String tablePrefix;

    /**
     * Whether to enable environment-based table separation
     */
    private boolean enableEnvironmentSeparation = true;

    /**
     * Whether to validate environment configuration on startup
     */
    private boolean validateOnStartup = true;

    /**
     * Custom separator between environment and table name
     */
    private String separator = "_";

    public DatabaseEnvironmentProperties() {
        // Default constructor
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
        // Auto-generate table prefix when environment changes
        if (environment != null) {
            this.tablePrefix = environment + separator;
        }
    }

    public String getTablePrefix() {
        return tablePrefix;
    }

    public void setTablePrefix(String tablePrefix) {
        this.tablePrefix = tablePrefix;
    }

    public boolean isEnableEnvironmentSeparation() {
        return enableEnvironmentSeparation;
    }

    public void setEnableEnvironmentSeparation(boolean enableEnvironmentSeparation) {
        this.enableEnvironmentSeparation = enableEnvironmentSeparation;
    }

    public boolean isValidateOnStartup() {
        return validateOnStartup;
    }

    public void setValidateOnStartup(boolean validateOnStartup) {
        this.validateOnStartup = validateOnStartup;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
        // Update table prefix if environment is set
        if (this.environment != null && this.tablePrefix != null) {
            this.tablePrefix = this.environment + separator;
        }
    }

    /**
     * Get the computed table prefix for the current environment
     * @return the table prefix to use for database tables
     */
    public String getComputedTablePrefix() {
        if (!enableEnvironmentSeparation) {
            return "";
        }
        
        if (tablePrefix != null) {
            return tablePrefix;
        }
        
        if (environment != null) {
            return environment + separator;
        }
        
        return "";
    }

    /**
     * Check if the current configuration is valid
     * @return true if configuration is valid
     */
    public boolean isValidConfiguration() {
        if (!enableEnvironmentSeparation) {
            return true;
        }
        
        return environment != null && !environment.trim().isEmpty() &&
               environment.matches("^(dev|prod|test)$");
    }

    @Override
    public String toString() {
        return "DatabaseEnvironmentProperties{" +
                "environment='" + environment + '\'' +
                ", tablePrefix='" + tablePrefix + '\'' +
                ", enableEnvironmentSeparation=" + enableEnvironmentSeparation +
                ", validateOnStartup=" + validateOnStartup +
                ", separator='" + separator + '\'' +
                '}';
    }
}