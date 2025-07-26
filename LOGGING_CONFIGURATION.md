# Logging Configuration

This document describes the comprehensive logging setup for the Survey Application backend.

## Overview

The application uses SLF4J with Logback for logging, providing:
- Console logging for development and Docker environments
- File logging for production debugging
- Configurable log levels via environment variables
- Structured logging with timestamps and context

## Log Levels

The following log levels are supported:
- **ERROR**: Critical errors that require immediate attention
- **WARN**: Warning conditions that should be investigated
- **INFO**: General information about application flow
- **DEBUG**: Detailed information for debugging
- **TRACE**: Very detailed information (rarely used)

## Environment Variables

All log levels can be configured via Docker Compose environment variables:

### Root and Application Logging
```yaml
- LOG_LEVEL_ROOT=INFO                    # Root logging level
- LOG_LEVEL_SURVEY=INFO                  # Main application package
- LOG_LEVEL_CONTROLLER=INFO              # Controller layer
- LOG_LEVEL_SERVICE=INFO                 # Service layer
- LOG_LEVEL_REPOSITORY=INFO              # Repository layer
- LOG_LEVEL_SECURITY=INFO                # Security components
- LOG_LEVEL_CONFIG=INFO                  # Configuration classes
```

### Framework Logging
```yaml
- LOG_LEVEL_SPRING_SECURITY=WARN         # Spring Security
- LOG_LEVEL_SPRING_WEB=INFO              # Spring Web
- LOG_LEVEL_SPRING_DATA=INFO             # Spring Data
- LOG_LEVEL_HIBERNATE=WARN               # Hibernate SQL
- LOG_LEVEL_HIBERNATE_BINDER=WARN        # Hibernate parameter binding
```

### File Logging Configuration
```yaml
- LOG_FILE_PATH=/app/backend/logs/survey-app.log
- LOG_FILE_MAX_SIZE=10MB
- LOG_FILE_MAX_HISTORY=7
```

## Logging Categories

### 1. Application Startup
- Application version and Java version
- Database initialization
- Sample data creation
- Service startup status

### 2. Authentication & Security
- Login attempts (success/failure)
- JWT token generation and validation
- User authentication events
- Security filter operations

### 3. API Operations
- HTTP request processing
- Controller method entry/exit
- Response generation
- Error handling

### 4. Business Logic
- Survey operations (create, read, update, delete)
- Response submissions
- User management operations
- Data validation

### 5. Database Operations
- SQL query execution (configurable)
- Transaction management
- Connection handling

## Usage Examples

### Development (Verbose Logging)
```yaml
environment:
  - LOG_LEVEL_ROOT=DEBUG
  - LOG_LEVEL_SURVEY=DEBUG
  - LOG_LEVEL_CONTROLLER=DEBUG
  - LOG_LEVEL_SERVICE=DEBUG
  - LOG_LEVEL_HIBERNATE=DEBUG
```

### Production (Minimal Logging)
```yaml
environment:
  - LOG_LEVEL_ROOT=WARN
  - LOG_LEVEL_SURVEY=INFO
  - LOG_LEVEL_CONTROLLER=WARN
  - LOG_LEVEL_SERVICE=INFO
  - LOG_LEVEL_HIBERNATE=WARN
```

### Debugging Specific Issues
```yaml
environment:
  - LOG_LEVEL_SECURITY=DEBUG              # Debug authentication issues
  - LOG_LEVEL_HIBERNATE=DEBUG             # Debug database issues
  - LOG_LEVEL_SPRING_WEB=DEBUG            # Debug web request issues
```

## Log Output

### Console Format
```
2025-07-26 12:30:45 [http-nio-8080-exec-1] INFO  c.s.controller.AuthController - Login attempt for user: admin
2025-07-26 12:30:45 [http-nio-8080-exec-1] DEBUG c.s.service.UserService - Loading user by username: admin
2025-07-26 12:30:45 [http-nio-8080-exec-1] INFO  c.s.controller.AuthController - Login successful for user: admin with role: ADMIN
```

### File Format
Same as console format, but written to log files with rotation.

## Monitoring and Debugging

### Viewing Logs in Docker
```bash
# View all logs
docker compose logs survey-app

# Follow logs in real-time
docker compose logs -f survey-app

# View recent logs
docker compose logs --tail 100 survey-app

# Filter by log level
docker compose logs survey-app | grep "ERROR\|WARN"
```

### Log Analysis
```bash
# Count log entries by level
docker compose logs survey-app | grep -o "ERROR\|WARN\|INFO\|DEBUG" | sort | uniq -c

# Find authentication failures
docker compose logs survey-app | grep "Login failed"

# Find slow database queries
docker compose logs survey-app | grep "Hibernate.*executed"
```

## Best Practices

1. **Use Appropriate Log Levels**:
   - ERROR: For exceptions and critical failures
   - WARN: For recoverable issues
   - INFO: For important business events
   - DEBUG: For detailed troubleshooting

2. **Include Context**:
   - User IDs for user-specific operations
   - Request IDs for request tracing
   - Operation parameters for debugging

3. **Avoid Sensitive Data**:
   - Never log passwords or tokens
   - Be careful with personal information
   - Use placeholders for sensitive data

4. **Performance Considerations**:
   - Use parameterized logging: `logger.debug("User: {}", userId)`
   - Avoid expensive operations in debug logs
   - Use appropriate log levels in production

## Troubleshooting

### Common Issues

1. **Too Much Logging**: Increase log levels to WARN or ERROR
2. **Not Enough Logging**: Decrease log levels to DEBUG
3. **Missing Logs**: Check if logs are being written to files vs console
4. **Performance Issues**: Reduce logging verbosity or use async logging

### Debugging Commands
```bash
# Check current log configuration
docker exec survey-project-survey-app-1 cat /app/backend/logs/survey-app.log

# Monitor specific log patterns
docker compose logs -f survey-app | grep "ERROR\|Exception"

# Check log file size and rotation
docker exec survey-project-survey-app-1 ls -la /app/backend/logs/
``` 