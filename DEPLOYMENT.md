# Deployment Guide

## Branch Strategy

### Branches
- **`main`** - Production branch (stable, deployed to production)
- **`dev`** - Development branch (active development, deployed to staging)

### Workflow
1. **Feature Development**: Work on `dev` branch
2. **Testing**: Test features on `dev` branch/staging environment
3. **Production Release**: Merge `dev` → `main` → triggers production deployment

## Environment Configuration

### 1. Development (Local)

- Uses H2 in-memory database
- Profile: `dev`
- Run: `./mvnw spring-boot:run`

### 2. Production Deployment

#### Option A: Using Environment Variables

Set these environment variables on your deployment platform:

```bash
# Database
DATABASE_URL=jdbc:postgresql://your-host:5432/your-database
DATABASE_USERNAME=your-username
DATABASE_PASSWORD=your-password
DATABASE_DRIVER=org.postgresql.Driver

# JPA
JPA_DDL_AUTO=validate
JPA_DIALECT=org.hibernate.dialect.PostgreSQLDialect
JPA_SHOW_SQL=false

# JWT
JWT_SECRET=your-super-secret-jwt-key-here
JWT_EXPIRATION=86400000

# Spring Profile
SPRING_PROFILES_ACTIVE=prod
```

#### Option B: Using application-prod.properties (Not Recommended)

Create `application-prod.properties` with actual values (DO NOT commit to Git):

```properties
spring.datasource.url=jdbc:postgresql://actual-host:5432/actual-db
spring.datasource.username=actual-username
spring.datasource.password=actual-password
```

## Platform-Specific Deployment

### Heroku

```bash
# Set environment variables
heroku config:set DATABASE_URL="your-postgres-url"
heroku config:set JWT_SECRET="your-jwt-secret"
heroku config:set SPRING_PROFILES_ACTIVE=prod

# Deploy
git push heroku main
```

### Railway

```bash
# In Railway dashboard, set environment variables:
DATABASE_URL=your-postgres-url
JWT_SECRET=your-jwt-secret
SPRING_PROFILES_ACTIVE=prod
```

### Render

```yaml
# render.yaml
services:
  - type: web
    name: medilog-api
    env: java
    buildCommand: ./mvnw clean package -DskipTests
    startCommand: java -jar target/medilog-0.0.1-SNAPSHOT.jar
    envVars:
      - key: DATABASE_URL
        value: your-postgres-url
      - key: JWT_SECRET
        generateValue: true
      - key: SPRING_PROFILES_ACTIVE
        value: prod
```

### Docker

```dockerfile
# Dockerfile
FROM openjdk:21-jdk-slim
COPY target/medilog-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

```bash
# Build and run
docker build -t medilog .
docker run -p 8080:8080 \
  -e DATABASE_URL="your-postgres-url" \
  -e JWT_SECRET="your-jwt-secret" \
  -e SPRING_PROFILES_ACTIVE=prod \
  medilog
```

## Security Best Practices

1. **Never commit sensitive data to Git**
2. **Use strong JWT secrets** (at least 256 bits)
3. **Set `JPA_DDL_AUTO=validate`** in production
4. **Use HTTPS** in production
5. **Rotate secrets regularly**

## Testing Deployment

```bash
# Health check
curl https://your-app.com/actuator/health

# Test signup
curl -X POST https://your-app.com/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Test","lastName":"User","email":"test@example.com","password":"password123"}'
```
