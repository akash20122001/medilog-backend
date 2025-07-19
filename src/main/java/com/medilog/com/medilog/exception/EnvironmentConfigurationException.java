package com.medilog.com.medilog.exception;

/**
 * Exception thrown when there are issues with environment configuration
 * for database table separation.
 */
public class EnvironmentConfigurationException extends RuntimeException {

    private final String environment;
    private final String configurationIssue;

    public EnvironmentConfigurationException(String message) {
        super(message);
        this.environment = null;
        this.configurationIssue = null;
    }

    public EnvironmentConfigurationException(String message, Throwable cause) {
        super(message, cause);
        this.environment = null;
        this.configurationIssue = null;
    }

    public EnvironmentConfigurationException(String message, String environment, String configurationIssue) {
        super(message);
        this.environment = environment;
        this.configurationIssue = configurationIssue;
    }

    public EnvironmentConfigurationException(String message, String environment, String configurationIssue, Throwable cause) {
        super(message, cause);
        this.environment = environment;
        this.configurationIssue = configurationIssue;
    }

    public String getEnvironment() {
        return environment;
    }

    public String getConfigurationIssue() {
        return configurationIssue;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        if (environment != null) {
            sb.append(" [Environment: ").append(environment).append("]");
        }
        if (configurationIssue != null) {
            sb.append(" [Issue: ").append(configurationIssue).append("]");
        }
        return sb.toString();
    }
}