package com.medilog.com.medilog.config;

import com.medilog.com.medilog.exception.EnvironmentConfigurationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnvironmentValidationStartupComponentTest {

    @Mock
    private DatabaseEnvironmentProperties databaseProperties;

    @Mock
    private Environment environment;

    @Mock
    private EnvironmentAwarePhysicalNamingStrategy namingStrategy;

    private EnvironmentValidationStartupComponent validationComponent;

    @BeforeEach
    void setUp() {
        validationComponent = new EnvironmentValidationStartupComponent(
                databaseProperties, environment, namingStrategy);
    }

    @Test
    void validateConfiguration_WithValidConfiguration_ShouldPass() {
        // Arrange
        when(databaseProperties.isValidateOnStartup()).thenReturn(true);
        when(databaseProperties.isEnableEnvironmentSeparation()).thenReturn(true);
        when(databaseProperties.isValidConfiguration()).thenReturn(true);
        when(databaseProperties.getEnvironment()).thenReturn("dev");
        when(databaseProperties.getComputedTablePrefix()).thenReturn("dev_");
        when(environment.getActiveProfiles()).thenReturn(new String[]{"dev"});
        when(environment.getDefaultProfiles()).thenReturn(new String[]{"default"});
        when(environment.getProperty("spring.datasource.url", "Not configured")).thenReturn("jdbc:postgresql://localhost:5432/testdb");
        when(environment.getProperty("spring.datasource.username", "Not configured")).thenReturn("testuser");
        when(environment.getProperty("spring.jpa.hibernate.ddl-auto", "Not configured")).thenReturn("update");
        when(environment.getProperty("spring.jpa.properties.hibernate.dialect", "Not configured")).thenReturn("org.hibernate.dialect.PostgreSQLDialect");

        // Act & Assert
        assertDoesNotThrow(() -> validationComponent.validateConfiguration());
    }

    @Test
    void validateConfiguration_WithInvalidEnvironment_ShouldThrowException() {
        // Arrange
        when(databaseProperties.isValidateOnStartup()).thenReturn(true);
        when(databaseProperties.isEnableEnvironmentSeparation()).thenReturn(true);
        when(databaseProperties.isValidConfiguration()).thenReturn(false);
        when(databaseProperties.getEnvironment()).thenReturn("invalid");
        when(databaseProperties.toString()).thenReturn("DatabaseEnvironmentProperties{environment='invalid'}");

        // Act & Assert
        EnvironmentConfigurationException exception = assertThrows(
                EnvironmentConfigurationException.class,
                () -> validationComponent.validateConfiguration()
        );

        assertTrue(exception.getMessage().contains("Invalid database environment configuration"));
        assertEquals("invalid", exception.getEnvironment());
        assertEquals("Invalid environment value", exception.getConfigurationIssue());
    }

    @Test
    void validateConfiguration_WithEnvironmentProfileMismatch_ShouldThrowException() {
        // Arrange
        when(databaseProperties.isValidateOnStartup()).thenReturn(true);
        when(databaseProperties.isEnableEnvironmentSeparation()).thenReturn(true);
        when(databaseProperties.isValidConfiguration()).thenReturn(true);
        when(databaseProperties.getEnvironment()).thenReturn("prod");
        when(environment.getActiveProfiles()).thenReturn(new String[]{"dev"});

        // Act & Assert
        EnvironmentConfigurationException exception = assertThrows(
                EnvironmentConfigurationException.class,
                () -> validationComponent.validateConfiguration()
        );

        assertTrue(exception.getMessage().contains("Database environment configuration mismatch"));
        assertEquals("prod", exception.getEnvironment());
        assertEquals("Environment-profile mismatch", exception.getConfigurationIssue());
    }

    @Test
    void validateConfiguration_WithEmptyTablePrefix_ShouldThrowException() {
        // Arrange
        when(databaseProperties.isValidateOnStartup()).thenReturn(true);
        when(databaseProperties.isEnableEnvironmentSeparation()).thenReturn(true);
        when(databaseProperties.isValidConfiguration()).thenReturn(true);
        when(databaseProperties.getEnvironment()).thenReturn("dev");
        when(databaseProperties.getComputedTablePrefix()).thenReturn("");
        when(environment.getActiveProfiles()).thenReturn(new String[]{"dev"});

        // Act & Assert
        EnvironmentConfigurationException exception = assertThrows(
                EnvironmentConfigurationException.class,
                () -> validationComponent.validateConfiguration()
        );

        assertTrue(exception.getMessage().contains("Table prefix cannot be null or empty"));
        assertEquals("dev", exception.getEnvironment());
        assertEquals("Empty table prefix", exception.getConfigurationIssue());
    }

    @Test
    void validateConfiguration_WithEnvironmentSeparationDisabled_ShouldLogWarningAndPass() {
        // Arrange
        when(databaseProperties.isValidateOnStartup()).thenReturn(true);
        when(databaseProperties.isEnableEnvironmentSeparation()).thenReturn(false);
        when(databaseProperties.getEnvironment()).thenReturn("dev");
        when(databaseProperties.getComputedTablePrefix()).thenReturn("dev_");
        when(environment.getActiveProfiles()).thenReturn(new String[]{"dev"});
        when(environment.getDefaultProfiles()).thenReturn(new String[]{"default"});
        when(environment.getProperty("spring.datasource.url", "Not configured")).thenReturn("jdbc:postgresql://localhost:5432/testdb");
        when(environment.getProperty("spring.datasource.username", "Not configured")).thenReturn("testuser");
        when(environment.getProperty("spring.jpa.hibernate.ddl-auto", "Not configured")).thenReturn("update");
        when(environment.getProperty("spring.jpa.properties.hibernate.dialect", "Not configured")).thenReturn("org.hibernate.dialect.PostgreSQLDialect");

        // Act & Assert
        assertDoesNotThrow(() -> validationComponent.validateConfiguration());
        
        // Verify that validation methods are not called when separation is disabled
        verify(databaseProperties, never()).isValidConfiguration();
    }

    @Test
    void validateConfiguration_WithValidationDisabled_ShouldSkipValidation() {
        // Arrange
        when(databaseProperties.isValidateOnStartup()).thenReturn(false);
        when(databaseProperties.getEnvironment()).thenReturn("dev");
        when(databaseProperties.isEnableEnvironmentSeparation()).thenReturn(true);
        when(databaseProperties.getComputedTablePrefix()).thenReturn("dev_");
        when(environment.getActiveProfiles()).thenReturn(new String[]{"dev"});
        when(environment.getDefaultProfiles()).thenReturn(new String[]{"default"});
        when(environment.getProperty("spring.datasource.url", "Not configured")).thenReturn("jdbc:postgresql://localhost:5432/testdb");
        when(environment.getProperty("spring.datasource.username", "Not configured")).thenReturn("testuser");
        when(environment.getProperty("spring.jpa.hibernate.ddl-auto", "Not configured")).thenReturn("update");
        when(environment.getProperty("spring.jpa.properties.hibernate.dialect", "Not configured")).thenReturn("org.hibernate.dialect.PostgreSQLDialect");

        // Act & Assert
        assertDoesNotThrow(() -> validationComponent.validateConfiguration());
        
        // Verify that validation is skipped (but logging methods are still called)
        verify(databaseProperties, never()).isValidConfiguration();
    }

    @Test
    void run_WithValidConfiguration_ShouldPass() throws Exception {
        // Arrange
        when(databaseProperties.getComputedTablePrefix()).thenReturn("dev_");

        // Act & Assert
        assertDoesNotThrow(() -> validationComponent.run());
    }

    @Test
    void run_WithRuntimeValidationFailure_ShouldThrowException() throws Exception {
        // Arrange
        when(databaseProperties.getComputedTablePrefix()).thenThrow(new RuntimeException("Test exception"));
        when(databaseProperties.getEnvironment()).thenReturn("dev");

        // Act & Assert
        EnvironmentConfigurationException exception = assertThrows(
                EnvironmentConfigurationException.class,
                () -> validationComponent.run()
        );

        assertTrue(exception.getMessage().contains("Runtime validation failed"));
        assertEquals("dev", exception.getEnvironment());
        assertEquals("Runtime validation failure", exception.getConfigurationIssue());
    }
}