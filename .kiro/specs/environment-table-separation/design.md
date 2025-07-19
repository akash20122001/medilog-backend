# Design Document

## Overview

This design implements environment-based table separation using Spring Boot's profile-based configuration and Hibernate's physical naming strategy. The solution uses table prefixes to distinguish between development and production data within the same PostgreSQL database, ensuring complete data isolation while maintaining a single codebase.

## Architecture

The solution leverages Spring Boot's existing profile system (`dev`, `prod`) and implements a custom Hibernate PhysicalNamingStrategy that automatically prefixes table names based on the active profile. This approach is transparent to the application code and requires minimal changes to existing entities and repositories.

### Key Components:
- **Environment-aware Physical Naming Strategy**: Automatically prefixes table names
- **Profile-based Configuration**: Uses existing Spring profiles for environment detection
- **Migration Strategy**: Handles schema creation for both environments
- **Configuration Properties**: Environment-specific database settings

## Components and Interfaces

### 1. EnvironmentAwarePhysicalNamingStrategy

```java
@Component
public class EnvironmentAwarePhysicalNamingStrategy implements PhysicalNamingStrategy {
    private final String tablePrefix;
    
    // Automatically prefixes all table names with environment identifier
    // Example: "users" becomes "dev_users" or "prod_users"
}
```

**Responsibilities:**
- Detect active Spring profile
- Apply appropriate table prefix to all entity table names
- Handle both explicit @Table annotations and default naming

### 2. Environment Configuration Properties

```java
@ConfigurationProperties(prefix = "app.database")
@Component
public class DatabaseEnvironmentProperties {
    private String environment;
    private String tablePrefix;
    // Environment-specific database configuration
}
```

**Responsibilities:**
- Provide environment-specific configuration
- Allow override of default prefixing behavior
- Support custom table prefix patterns

### 3. Database Migration Configuration

```java
@Configuration
public class DatabaseMigrationConfig {
    // Configure Flyway/Liquibase for environment-specific migrations
    // Handle schema initialization for both environments
}
```

**Responsibilities:**
- Ensure proper schema creation for each environment
- Handle migration versioning per environment
- Support rollback scenarios

## Data Models

### Current Entity Structure
The existing `User` entity will remain unchanged:

```java
@Entity
@Table(name = "users")  // Will become "dev_users" or "prod_users" automatically
public class User {
    // Existing fields remain the same
}
```

### Resulting Database Schema

**Development Environment:**
- Table: `dev_users`
- Indexes: `dev_users_email_idx`
- Sequences: `dev_users_id_seq`

**Production Environment:**
- Table: `prod_users`
- Indexes: `prod_users_email_idx`
- Sequences: `prod_users_id_seq`

## Implementation Strategy

### Phase 1: Core Infrastructure
1. Implement `EnvironmentAwarePhysicalNamingStrategy`
2. Configure Hibernate to use the custom naming strategy
3. Add environment detection logic

### Phase 2: Configuration Integration
1. Update application properties for environment-specific settings
2. Implement configuration properties class
3. Add validation for environment setup

### Phase 3: Migration Support
1. Configure database migration tools for environment-aware operation
2. Create initial migration scripts for both environments
3. Add migration validation

### Phase 4: Testing and Validation
1. Create integration tests for both environments
2. Validate data isolation
3. Test environment switching

## Configuration Changes

### application.properties Updates

```properties
# Environment-specific table prefixing
app.database.environment=${SPRING_PROFILES_ACTIVE:dev}
app.database.table-prefix=${app.database.environment}_

# Hibernate naming strategy
spring.jpa.hibernate.naming.physical-strategy=com.medilog.com.medilog.config.EnvironmentAwarePhysicalNamingStrategy
```

### Profile-specific Properties

**application-dev.properties:**
```properties
app.database.environment=dev
spring.jpa.hibernate.ddl-auto=update
```

**application-prod.properties:**
```properties
app.database.environment=prod
spring.jpa.hibernate.ddl-auto=validate
```

## Error Handling

### Environment Misconfiguration
- Validate environment configuration on startup
- Fail fast if environment cannot be determined
- Provide clear error messages for configuration issues

### Database Connection Issues
- Handle connection failures gracefully
- Provide environment-specific error context
- Support fallback mechanisms for development

### Migration Failures
- Detect and report environment-specific migration issues
- Prevent cross-environment data corruption
- Support rollback scenarios

## Testing Strategy

### Unit Tests
- Test naming strategy with different profiles
- Validate configuration property binding
- Test error handling scenarios

### Integration Tests
- Test complete environment separation
- Validate data isolation between environments
- Test environment switching scenarios

### Test Environment Setup
```properties
# application-test.properties
app.database.environment=test
spring.jpa.hibernate.ddl-auto=create-drop
```

## Security Considerations

### Data Isolation
- Ensure complete separation between environments
- Prevent accidental cross-environment queries
- Validate table access patterns

### Configuration Security
- Secure environment variable handling
- Validate database connection parameters
- Implement proper credential management

## Performance Considerations

### Database Performance
- Monitor impact of table prefixing on query performance
- Ensure proper indexing for environment-specific tables
- Consider connection pooling implications

### Application Startup
- Minimize overhead of environment detection
- Cache naming strategy decisions
- Optimize configuration loading

## Deployment Considerations

### Environment Variables
```bash
# Development
SPRING_PROFILES_ACTIVE=dev

# Production  
SPRING_PROFILES_ACTIVE=prod
```

### Database Setup
- Both environments share the same database connection
- Tables are automatically created with appropriate prefixes
- No manual schema setup required

## Monitoring and Observability

### Logging
- Log active environment on startup
- Log table name resolution for debugging
- Monitor cross-environment access attempts

### Metrics
- Track environment-specific database operations
- Monitor table usage patterns
- Alert on configuration issues