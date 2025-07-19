package com.medilog.com.medilog.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class DatabaseEnvironmentPropertiesTest {

    @Autowired
    private DatabaseEnvironmentProperties databaseEnvironmentProperties;

    @Test
    void testConfigurationPropertiesLoaded() {
        assertNotNull(databaseEnvironmentProperties);
        assertEquals("test", databaseEnvironmentProperties.getEnvironment());
        assertEquals("test_", databaseEnvironmentProperties.getTablePrefix());
        assertTrue(databaseEnvironmentProperties.isEnableEnvironmentSeparation());
        assertTrue(databaseEnvironmentProperties.isValidateOnStartup());
        assertEquals("_", databaseEnvironmentProperties.getSeparator());
    }

    @Test
    void testComputedTablePrefix() {
        assertEquals("test_", databaseEnvironmentProperties.getComputedTablePrefix());
    }

    @Test
    void testValidConfiguration() {
        assertTrue(databaseEnvironmentProperties.isValidConfiguration());
    }

    @Test
    void testEnvironmentValidation() {
        DatabaseEnvironmentProperties props = new DatabaseEnvironmentProperties();
        
        // Test valid environments
        props.setEnvironment("dev");
        assertTrue(props.isValidConfiguration());
        
        props.setEnvironment("prod");
        assertTrue(props.isValidConfiguration());
        
        props.setEnvironment("test");
        assertTrue(props.isValidConfiguration());
        
        // Test invalid environment
        props.setEnvironment("invalid");
        assertFalse(props.isValidConfiguration());
    }

    @Test
    void testTablePrefixGeneration() {
        DatabaseEnvironmentProperties props = new DatabaseEnvironmentProperties();
        
        props.setEnvironment("dev");
        assertEquals("dev_", props.getComputedTablePrefix());
        
        props.setEnvironment("prod");
        assertEquals("prod_", props.getComputedTablePrefix());
        
        // Test custom separator
        props.setSeparator("-");
        assertEquals("prod-", props.getComputedTablePrefix());
    }

    @Test
    void testDisabledEnvironmentSeparation() {
        DatabaseEnvironmentProperties props = new DatabaseEnvironmentProperties();
        props.setEnableEnvironmentSeparation(false);
        
        assertEquals("", props.getComputedTablePrefix());
        assertTrue(props.isValidConfiguration());
    }
}