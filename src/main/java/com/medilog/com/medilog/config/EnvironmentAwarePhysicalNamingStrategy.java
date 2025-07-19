package com.medilog.com.medilog.config;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Environment-aware physical naming strategy that prefixes table names
 * based on the active Spring profile to enable environment separation
 * within a single database.
 */
@Component
public class EnvironmentAwarePhysicalNamingStrategy implements PhysicalNamingStrategy {

    private final Environment environment;
    private String cachedTablePrefix;

    @Autowired
    public EnvironmentAwarePhysicalNamingStrategy(Environment environment) {
        this.environment = environment;
    }

    /**
     * Gets the table prefix based on the active Spring profile.
     * Caches the result to avoid repeated profile lookups.
     * 
     * @return the table prefix (e.g., "dev_", "prod_", "test_")
     */
    private String getTablePrefix() {
        if (cachedTablePrefix == null) {
            String[] activeProfiles = environment.getActiveProfiles();
            
            // Default to "dev" if no profiles are active
            String activeProfile = "dev";
            
            if (activeProfiles.length > 0) {
                // Use the first active profile
                activeProfile = activeProfiles[0];
            }
            
            // Create prefix with underscore
            cachedTablePrefix = activeProfile + "_";
        }
        
        return cachedTablePrefix;
    }

    @Override
    public Identifier toPhysicalCatalogName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        // Return as-is for catalog names
        return name;
    }

    @Override
    public Identifier toPhysicalSchemaName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        // Return as-is for schema names
        return name;
    }

    @Override
    public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        if (name == null) {
            return null;
        }
        
        String tableName = name.getText();
        String prefix = getTablePrefix();
        
        // Check if the table name already has the prefix to avoid double-prefixing
        if (!tableName.startsWith(prefix)) {
            tableName = prefix + tableName;
        }
        
        return Identifier.toIdentifier(tableName);
    }

    @Override
    public Identifier toPhysicalSequenceName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        if (name == null) {
            return null;
        }
        
        String sequenceName = name.getText();
        String prefix = getTablePrefix();
        
        // Apply prefix to sequence names as well
        if (!sequenceName.startsWith(prefix)) {
            sequenceName = prefix + sequenceName;
        }
        
        return Identifier.toIdentifier(sequenceName);
    }

    @Override
    public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        // Return column names as-is (no prefixing needed)
        return name;
    }
}