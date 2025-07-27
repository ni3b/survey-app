# Survey Application

A full-stack survey web application built with Spring Boot (backend) and React (frontend), designed for creating and managing surveys with user participation and analytics.

## 🚀 Quick Start

### Option 1: Docker (Recommended)

**Prerequisites:**
- Docker (version 20.10 or higher)
- Docker Compose (version 2.0 or higher)

```bash
# Make the script executable
chmod +x build-and-run.sh

# Build and run the application
./build-and-run.sh
```

**Access the application:**
- **Frontend**: http://localhost
- **API Documentation**: http://localhost/swagger-ui
- **Health Check**: http://localhost/health

### Option 2: Local Development

**Prerequisites:**
- Java 17 or higher
- Node.js 18 or higher
- npm or yarn

**Backend Setup:**
```bash
cd backend
./mvnw clean install
./mvnw spring-boot:run
```

**Frontend Setup:**
```bash
cd frontend
npm install
npm start
```

## 📋 Features

### User Features
- User login with JWT authentication (accounts created by admin)
- Browse and participate in active surveys
- View questions one at a time or as a list
- See top 5 responses with upvotes for each question
- Submit new responses (authentication required)
- Upvote responses (one per user per response)
- Real-time updates after interactions
- Responsive design for desktop and mobile

### Admin Features
- Secure admin login
- Dashboard with survey analytics
- Survey management (create, edit, delete, schedule)
- Question management within surveys
- User management and account creation
- Export survey data to CSV/Excel
- View all responses and upvotes
- Analytics and engagement trends

## 🏗️ Architecture

The application uses a modern full-stack architecture:

- **Frontend**: React.js application with TypeScript and Material-UI
- **Backend**: Spring Boot application with JPA/Hibernate
- **Database**: SQLite (development) / PostgreSQL/MySQL (production)
- **Reverse Proxy**: Nginx (Docker deployment)
- **Authentication**: JWT-based with Spring Security

### Docker Architecture
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Nginx Proxy   │───▶│  Spring Boot    │───▶│   SQLite DB     │
│   (Port 80)     │    │   (Port 8080)   │    │   (Volume)      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │
         ▼                       ▼
┌─────────────────┐    ┌─────────────────┐
│  React Frontend │    │   Static Files  │
│   (Built)       │    │   (Served)      │
└─────────────────┘    └─────────────────┘
```

## 🛠️ Tech Stack

### Backend
- **Framework**: Spring Boot 3.x
- **Database**: SQLite (H2 for testing)
- **Security**: Spring Security with JWT (HS512)
- **API Documentation**: OpenAPI/Swagger
- **Build Tool**: Maven
- **Logging**: SLF4J with Logback

### Frontend
- **Framework**: React 18 with TypeScript
- **UI Library**: Material-UI (MUI)
- **State Management**: React Context + Hooks
- **Charts**: Recharts for analytics
- **HTTP Client**: Axios
- **Build Tool**: Vite

### DevOps
- **Containerization**: Docker with multi-stage builds
- **Reverse Proxy**: Nginx
- **Process Management**: Docker Compose

## 📁 Project Structure

```
survey-project/
├── backend/                 # Spring Boot application
│   ├── src/main/java/com/survey/
│   │   ├── controller/      # REST API controllers
│   │   ├── model/          # JPA entities
│   │   ├── repository/     # Data access layer
│   │   ├── service/        # Business logic
│   │   ├── security/       # JWT authentication
│   │   ├── config/         # Configuration classes
│   │   ├── dto/           # Data transfer objects
│   │   └── exception/     # Custom exceptions
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   ├── logback-spring.xml
│   │   └── schema.sql
│   └── pom.xml
├── frontend/                # React application
│   ├── src/
│   │   ├── components/     # Reusable UI components
│   │   ├── pages/         # Page components
│   │   ├── services/      # API service layer
│   │   ├── hooks/         # Custom React hooks
│   │   ├── types/         # TypeScript type definitions
│   │   ├── context/       # React context providers
│   │   └── providers/     # App providers
│   ├── package.json
│   └── tsconfig.json
├── Dockerfile              # Multi-stage Docker build
├── docker-compose.yml      # Docker Compose configuration
├── nginx.conf.template     # Nginx configuration
├── build-and-run.sh       # Build and run script
└── README.md
```

## 🔧 Configuration

### Environment Variables

#### Docker Environment Variables
```yaml
environment:
  # Application
  - SPRING_PROFILES_ACTIVE=docker
  - JAVA_OPTS=-Xmx512m -Xms256m
  
  # Security
  - JWT_SECRET=your-secure-jwt-secret-key
  
  # Logging (see Logging Configuration section)
  - LOG_LEVEL_ROOT=INFO
  - LOG_LEVEL_SURVEY=INFO
  - LOG_LEVEL_CONTROLLER=INFO
  - LOG_LEVEL_SERVICE=INFO
  - LOG_LEVEL_REPOSITORY=INFO
  - LOG_LEVEL_SECURITY=INFO
  - LOG_LEVEL_HIBERNATE=WARN
```

#### JWT Security Configuration
The application uses HS512 algorithm for JWT signing, requiring a minimum of 512 bits (64 characters) for the secret key.

**For Production:**
```bash
# Generate a secure JWT secret
./generate-jwt-secret.sh

# Set environment variable
export JWT_SECRET="your-generated-secret"
```

### Database Configuration
- **Development**: SQLite with automatic schema creation
- **Production**: PostgreSQL or MySQL (configure in application.properties)
- **Persistence**: Database file mounted as volume in Docker

### Default Credentials
- **Admin Username**: admin
- **Admin Password**: admin123

## 📊 Logging Configuration

The application uses comprehensive logging with configurable log levels via Docker Compose environment variables.

### Available Log Levels
- **ERROR**: Critical errors requiring immediate attention
- **WARN**: Warning conditions that should be investigated
- **INFO**: General information about application flow
- **DEBUG**: Detailed information for debugging
- **TRACE**: Very detailed information (rarely used)

### Logging Categories
```yaml
# Application Logging
- LOG_LEVEL_ROOT=INFO                    # Root logging level
- LOG_LEVEL_SURVEY=INFO                  # Main application package
- LOG_LEVEL_CONTROLLER=INFO              # Controller layer
- LOG_LEVEL_SERVICE=INFO                 # Service layer
- LOG_LEVEL_REPOSITORY=INFO              # Repository layer
- LOG_LEVEL_SECURITY=INFO                # Security components
- LOG_LEVEL_CONFIG=INFO                  # Configuration classes

# Framework Logging
- LOG_LEVEL_SPRING_SECURITY=WARN         # Spring Security
- LOG_LEVEL_SPRING_WEB=INFO              # Spring Web
- LOG_LEVEL_HIBERNATE=WARN               # Hibernate SQL
```

### Usage Examples
```bash
# Development (verbose logging)
docker compose up -d -e LOG_LEVEL_ROOT=DEBUG -e LOG_LEVEL_SURVEY=DEBUG

# Production (minimal logging)
docker compose up -d -e LOG_LEVEL_ROOT=WARN -e LOG_LEVEL_SURVEY=INFO

# Debug specific issues
docker compose up -d -e LOG_LEVEL_SECURITY=DEBUG -e LOG_LEVEL_HIBERNATE=DEBUG
```

### Viewing Logs
```bash
# View all logs
docker compose logs survey-app

# Follow logs in real-time
docker compose logs -f survey-app

# View recent logs
docker logs --tail 50 <container-id>
```

## 🔌 API Documentation

### Access Points
- **Swagger UI**: http://localhost/swagger-ui (Docker) or http://localhost:8080/swagger-ui.html (local)
- **OpenAPI JSON**: http://localhost/v3/api-docs (Docker) or http://localhost:8080/v3/api-docs (local)

### Key Endpoints

#### Public Endpoints
- `GET /api/surveys/active` - List active surveys
- `GET /api/surveys/{id}` - Get survey details
- `POST /api/responses` - Submit a response
- `POST /api/responses/{id}/upvote` - Upvote a response

#### Admin Endpoints
- `POST /api/auth/login` - Admin login
- `GET /api/admin/surveys` - List all surveys (admin)
- `POST /api/admin/surveys` - Create survey
- `PUT /api/admin/surveys/{id}` - Update survey
- `DELETE /api/admin/surveys/{id}` - Delete survey
- `GET /api/admin/analytics` - Get analytics data
- `GET /api/admin/export/{surveyId}` - Export survey data

## 🔒 Security Features

### Authentication & Authorization
- **JWT-based authentication** with HS512 algorithm
- **Role-based access control** (ADMIN vs USER roles)
- **Secure password handling** with BCrypt encryption
- **Token validation and expiration**
- **CORS configuration** for cross-origin requests

### Security Headers
- XSS protection
- Content type sniffing prevention
- Security headers via nginx
- Rate limiting to prevent abuse

### Production Security
- Non-root user execution in Docker
- Input validation and sanitization
- SQL injection prevention through JPA/Hibernate
- Secure JWT secret key generation

## 🗄️ Database Management

### Persistence
The SQLite database is mounted as a volume to ensure data persistence:
```yaml
volumes:
  - ./survey.db:/app/backend/survey.db
```

### Backup
```bash
# Stop the application
docker compose down

# Backup database
cp survey.db survey.db.backup

# Restart the application
docker compose up -d
```

### Reset Database
```bash
# WARNING: This will delete all data
rm survey.db
docker compose up -d
```

## 🐛 Troubleshooting

### Common Issues

#### Application Won't Start
```bash
# Check if port 80 is available
sudo lsof -i :80

# Check Docker logs
docker compose logs survey-app
```

#### Database Issues
```bash
# Check database file permissions
ls -la survey.db

# Reset database (WARNING: This will delete all data)
rm survey.db
docker compose up -d
```

#### Memory Issues
If the application runs out of memory, increase the JVM heap size:
```yaml
environment:
  - JAVA_OPTS=-Xmx1g -Xms512m
```

#### CORS Errors
Ensure backend is running and CORS is properly configured in application.properties.

### Performance Optimization
1. **Increase JVM Memory**: Adjust `JAVA_OPTS` for your server capacity
2. **Nginx Caching**: Configure nginx caching for static assets
3. **Database Optimization**: Consider database indexing and query optimization
4. **Load Balancing**: Use multiple instances behind a load balancer

## 🚀 Deployment

### Production Considerations
1. **Change JWT Secret**: Update the JWT secret via environment variable
2. **Use HTTPS**: Configure SSL/TLS certificates
3. **Database**: Consider using a production database (PostgreSQL, MySQL)
4. **Monitoring**: Add proper monitoring and alerting
5. **Backup Strategy**: Implement automated database backups

### Docker Production Build
```bash
# Build optimized production image
docker compose build --no-cache

# Run with production settings
docker compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

## 🤝 Contributing

1. Follow the existing code structure and naming conventions
2. Add appropriate tests for new features
3. Update documentation as needed
4. Ensure all endpoints are properly documented in Swagger
5. Test the Docker build process locally

## 📝 Development

### Adding New Features
- **Backend**: Add controllers, services, and repositories as needed
- **Frontend**: Create new components and pages in the appropriate directories
- **Database**: Add new entities and update the schema

### Testing
```bash
# Backend tests
cd backend && ./mvnw test

# Frontend tests
cd frontend && npm test
```

### Rebuilding After Changes
```bash
# Enable bake for better build performance
export COMPOSE_BAKE=true

# Rebuild and restart
docker compose down
docker compose build --no-cache
docker compose up -d
```

## 📄 License

This project is for educational and demonstration purposes.

---

For detailed logging configuration, see the Logging Configuration section above.
For Docker-specific setup, see the Docker Architecture section above. 