# Survey Application Backend

A Spring Boot backend for the Survey Application, providing RESTful APIs for survey management and user participation.

## Features

- **RESTful APIs**: Complete CRUD operations for surveys, questions, and responses
- **JWT Authentication**: Secure admin authentication with JWT tokens
- **SQLite Database**: Lightweight database with automatic schema creation
- **CORS Support**: Cross-origin resource sharing for frontend integration
- **Data Export**: CSV export functionality for survey data
- **Analytics**: Comprehensive statistics and reporting APIs
- **Swagger Documentation**: Interactive API documentation

## Tech Stack

- **Spring Boot 3.x** with Java 17
- **Spring Security** with JWT authentication
- **Spring Data JPA** with Hibernate
- **SQLite** database
- **OpenAPI/Swagger** for API documentation
- **OpenCSV** for data export

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Installation

1. Navigate to the backend directory:
   ```bash
   cd backend
   ```

2. Build the project:
   ```bash
   ./mvnw clean install
   ```

3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

The application will start on `http://localhost:8080`

### Database Setup

The application uses SQLite with automatic schema creation. The database file (`survey.db`) will be created automatically in the project root directory.

### Default Admin Credentials

- **Username**: admin
- **Password**: admin123

## Project Structure

```
src/main/java/com/survey/
├── config/              # Configuration classes
│   ├── SecurityConfig.java      # Security configuration
│   └── DataInitializer.java     # Database initialization
├── controller/          # REST controllers
│   ├── AuthController.java      # Authentication endpoints
│   ├── SurveyController.java    # Public survey endpoints
│   └── AdminController.java     # Admin management endpoints
├── dto/                 # Data Transfer Objects
│   ├── AuthDto.java     # Authentication DTOs
│   ├── SurveyDto.java   # Survey DTOs
│   ├── QuestionDto.java # Question DTOs
│   └── ResponseDto.java # Response DTOs
├── model/               # Entity models
│   ├── User.java        # User entity
│   ├── Survey.java      # Survey entity
│   ├── Question.java    # Question entity
│   ├── Response.java    # Response entity
│   └── Upvote.java      # Upvote entity
├── repository/          # Data access layer
│   ├── UserRepository.java      # User repository
│   ├── SurveyRepository.java    # Survey repository
│   ├── QuestionRepository.java  # Question repository
│   ├── ResponseRepository.java  # Response repository
│   └── UpvoteRepository.java    # Upvote repository
├── security/            # Security components
│   ├── JwtTokenProvider.java    # JWT token management
│   └── JwtAuthenticationFilter.java # JWT authentication filter
├── service/             # Business logic layer
│   ├── UserService.java         # User service
│   ├── SurveyService.java       # Survey service
│   └── ResponseService.java     # Response service
└── SurveyApplication.java       # Main application class
```

## API Endpoints

### Public Endpoints

#### Surveys
- `GET /api/surveys/active` - Get all active surveys
- `GET /api/surveys/{id}` - Get survey by ID

#### Responses
- `POST /api/responses` - Submit a response
- `POST /api/responses/{id}/upvote` - Upvote a response
- `DELETE /api/responses/{id}/upvote` - Remove upvote

### Admin Endpoints

#### Authentication
- `POST /api/auth/login` - Admin login
- `GET /api/auth/validate` - Validate JWT token

#### Survey Management
- `GET /api/admin/surveys` - Get all surveys (admin)
- `POST /api/admin/surveys` - Create new survey
- `PUT /api/admin/surveys/{id}` - Update survey
- `DELETE /api/admin/surveys/{id}` - Delete survey

#### Analytics
- `GET /api/admin/analytics` - Get analytics data
- `GET /api/admin/export/{surveyId}` - Export survey data

## Configuration

### Application Properties

The application uses a unified `application.properties` file that works for both local development and Docker environments. Key configuration options:

```properties
# Database (supports environment variables)
spring.datasource.url=${DATABASE_URL:jdbc:sqlite:survey.db?mode=rwc}
spring.jpa.hibernate.ddl-auto=${DDL_AUTO:update}

# JWT (supports environment variables)
jwt.secret=${JWT_SECRET:your-secret-key-here}
jwt.expiration=${JWT_EXPIRATION:86400000}

# CORS (supports environment variables)
spring.web.cors.allowed-origins=${CORS_ALLOWED_ORIGINS:http://localhost:3000}

# Swagger
springdoc.swagger-ui.path=/swagger-ui.html
```

### Environment Variables

The application supports the following environment variables for configuration:

```bash
# Database
export DATABASE_URL=jdbc:sqlite:/path/to/survey.db?mode=rwc

# Security
export JWT_SECRET=your-production-secret
export JWT_EXPIRATION=86400000

# CORS
export CORS_ALLOWED_ORIGINS=http://localhost:3000

# Logging
export LOG_LEVEL_SURVEY=INFO
export LOG_LEVEL_SECURITY=WARN
export LOG_LEVEL_HIBERNATE=WARN

# Server
export SERVER_PORT=8080
```

## Database Schema

### Entities

1. **User**: Admin users with authentication
2. **Survey**: Survey metadata and configuration
3. **Question**: Survey questions with types and settings
4. **Response**: User responses to questions
5. **Upvote**: User upvotes on responses

### Relationships

- Survey has many Questions (One-to-Many)
- Question has many Responses (One-to-Many)
- Response has many Upvotes (One-to-Many)
- User has many Upvotes (One-to-Many)

## Security

### JWT Authentication

- JWT tokens for admin authentication
- Token expiration and validation
- Secure password handling
- Role-based access control

### CORS Configuration

Configured to allow requests from the frontend application running on `http://localhost:3000`.

## Data Export

### CSV Export

The application supports exporting survey data to CSV format:

- All responses for a survey
- Question-wise response breakdown
- Upvote statistics

## Testing

### Running Tests

```bash
./mvnw test
```

### Test Coverage

The application includes unit tests for:
- Service layer business logic
- Repository data access
- Controller endpoint validation
- Security configuration

## Monitoring and Logging

### Logging Configuration

- DEBUG level for development
- Structured logging with timestamps
- Error tracking and reporting

### Health Checks

- Application health endpoint
- Database connectivity check
- JWT token validation

## Deployment

### Production Considerations

1. **Database**: Consider using PostgreSQL or MySQL for production
2. **Security**: Use strong JWT secrets and HTTPS
3. **CORS**: Configure allowed origins for production domains
4. **Logging**: Configure appropriate log levels
5. **Monitoring**: Add application monitoring and metrics

### Docker Support

The application can be containerized using Docker:

```dockerfile
FROM openjdk:17-jdk-slim
COPY target/survey-backend-*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

## Troubleshooting

### Common Issues

1. **Port conflicts**: Change server.port in application.properties
2. **Database issues**: Delete survey.db to reset the database
3. **CORS errors**: Verify CORS configuration matches frontend URL
4. **JWT issues**: Check JWT secret and expiration settings

### Debug Mode

Enable debug logging by adding to application.properties:

```properties
logging.level.com.survey=DEBUG
logging.level.org.springframework.security=DEBUG
```

## API Documentation

Once the application is running, access the interactive API documentation at:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Contributing

1. Follow Spring Boot best practices
2. Add appropriate validation and error handling
3. Include unit tests for new features
4. Update API documentation
5. Follow the existing code structure

## License

This project is for educational and demonstration purposes. 