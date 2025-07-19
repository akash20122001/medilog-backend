package com.medilog.com.medilog.config;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnvironmentAwarePhysicalNamingStrategyTest {

    @Mock
    private Environment environment;

    @Mock
    private JdbcEnvironment jdbcEnvironment;

    private EnvironmentAwarePhysicalNamingStrategy namingStrategy;

    @BeforeEach
    void setUp() {
        namingStrategy = new EnvironmentAwarePhysicalNamingStrategy(environment);
    }

    @Test
    void shouldPrefixTableNameWithDevProfile() {
        // Given
        when(environment.getActiveProfiles()).thenReturn(new String[]{"dev"});
        Identifier tableName = Identifier.toIdentifier("users");

        // When
        Identifier result = namingStrategy.toPhysicalTableName(tableName, jdbcEnvironment);

        // Then
        assertEquals("dev_users", result.getText());
    }

    @Test
    void shouldPrefixTableNameWithProdProfile() {
        // Given
        when(environment.getActiveProfiles()).thenReturn(new String[]{"prod"});
        Identifier tableName = Identifier.toIdentifier("users");

        // When
        Identifier result = namingStrategy.toPhysicalTableName(tableName, jdbcEnvironment);

        // Then
        assertEquals("prod_users", result.getText());
    }

    @Test
    void shouldDefaultToDevWhenNoActiveProfiles() {
        // Given
        when(environment.getActiveProfiles()).thenReturn(new String[]{});
        Identifier tableName = Identifier.toIdentifier("users");

        // When
        Identifier result = namingStrategy.toPhysicalTableName(tableName, jdbcEnvironment);

        // Then
        assertEquals("dev_users", result.getText());
    }

    @Test
    void shouldNotDoublePrefixTableName() {
        // Given
        when(environment.getActiveProfiles()).thenReturn(new String[]{"dev"});
        Identifier tableName = Identifier.toIdentifier("dev_users");

        // When
        Identifier result = namingStrategy.toPhysicalTableName(tableName, jdbcEnvironment);

        // Then
        assertEquals("dev_users", result.getText());
    }

    @Test
    void shouldPrefixSequenceName() {
        // Given
        when(environment.getActiveProfiles()).thenReturn(new String[]{"prod"});
        Identifier sequenceName = Identifier.toIdentifier("users_id_seq");

        // When
        Identifier result = namingStrategy.toPhysicalSequenceName(sequenceName, jdbcEnvironment);

        // Then
        assertEquals("prod_users_id_seq", result.getText());
    }

    @Test
    void shouldReturnColumnNameAsIs() {
        // Given
        when(environment.getActiveProfiles()).thenReturn(new String[]{"dev"});
        Identifier columnName = Identifier.toIdentifier("email");

        // When
        Identifier result = namingStrategy.toPhysicalColumnName(columnName, jdbcEnvironment);

        // Then
        assertEquals("email", result.getText());
    }

    @Test
    void shouldReturnCatalogNameAsIs() {
        // Given
        Identifier catalogName = Identifier.toIdentifier("medilog_db");

        // When
        Identifier result = namingStrategy.toPhysicalCatalogName(catalogName, jdbcEnvironment);

        // Then
        assertEquals("medilog_db", result.getText());
    }

    @Test
    void shouldReturnSchemaNameAsIs() {
        // Given
        Identifier schemaName = Identifier.toIdentifier("public");

        // When
        Identifier result = namingStrategy.toPhysicalSchemaName(schemaName, jdbcEnvironment);

        // Then
        assertEquals("public", result.getText());
    }

    @Test
    void shouldHandleNullTableName() {
        // Given
        when(environment.getActiveProfiles()).thenReturn(new String[]{"dev"});

        // When
        Identifier result = namingStrategy.toPhysicalTableName(null, jdbcEnvironment);

        // Then
        assertNull(result);
    }

    @Test
    void shouldHandleNullSequenceName() {
        // Given
        when(environment.getActiveProfiles()).thenReturn(new String[]{"dev"});

        // When
        Identifier result = namingStrategy.toPhysicalSequenceName(null, jdbcEnvironment);

        // Then
        assertNull(result);
    }

    @Test
    void shouldUseFirstActiveProfileWhenMultipleProfilesActive() {
        // Given
        when(environment.getActiveProfiles()).thenReturn(new String[]{"prod", "monitoring", "ssl"});
        Identifier tableName = Identifier.toIdentifier("users");

        // When
        Identifier result = namingStrategy.toPhysicalTableName(tableName, jdbcEnvironment);

        // Then
        assertEquals("prod_users", result.getText());
    }
}