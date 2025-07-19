package com.medilog.com.medilog.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
class DatabaseEnvironmentPropertiesIntegrationTest {
    
    @Autowired
    private DatabaseEnvironmentProperties databaseEnvironmentProperties;

    @Test
    void testDevProfileConfiguration() {
        assertEquals("dev", databaseEnvironmentProperties.getEnvironment());
        assertEquals("dev_", databaseEnvironmentProperties.getTablePrefix());
        assertEquals("dev_", databaseEnvironmentProperties.getComputedTablePrefix());
        assertTrue(databaseEnvironmentProperties.isValidConfiguration());
        assertTrue(databaseEnvironmentProperties.isEnableEnvironmentSeparation());
        assertTrue(databaseEnvironmentProperties.isValidateOnStartup());
    }
}