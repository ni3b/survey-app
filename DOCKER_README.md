# Survey Application - Docker Setup

This document explains how to build and run the Survey Application using Docker with nginx as a reverse proxy.

## 🏗️ Architecture

The application uses a multi-stage Docker build with the following components:

- **Frontend**: React.js application built with Node.js
- **Backend**: Spring Boot application built with Maven
- **Nginx**: Reverse proxy server that routes requests and serves static files
- **Database**: SQLite database (mounted as volume for persistence)

## 📁 File Structure

```
survey-project/
├── Dockerfile                 # Multi-stage Docker build
├── docker-compose.yml         # Docker Compose configuration
├── nginx.conf                 # Nginx reverse proxy configuration
├── start.sh                   # Startup script for the container
├── build-and-run.sh          # Build and run script
├── .dockerignore             # Files to exclude from Docker build
├── backend/
│   └── src/main/resources/
│       └── application.properties         # Unified configuration
├── frontend/
│   └── ...                   # React application
└── survey.db                 # SQLite database
```

## 🚀 Quick Start

### Prerequisites

- Docker (version 20.10 or higher)
- Docker Compose (version 2.0 or higher)

### Option 1: Using the Build Script (Recommended)

```bash
# Make the script executable (if not already)
chmod +x build-and-run.sh

# Build and run the application (with bake optimization)
./build-and-run.sh
```

**Note**: The build script automatically enables `COMPOSE_BAKE=true` for faster builds using Docker's bake feature.

### Option 2: Manual Docker Commands

```bash
# Enable bake for better build performance (optional)
export COMPOSE_BAKE=true

# Build the Docker image
docker compose build

# Start the application
docker compose up -d

# View logs
docker compose logs -f

# Stop the application
docker compose down
```

## 🌐 Access Points

Once the application is running, you can access it at:

- **Frontend Application**: http://localhost
- **API Endpoints**: http://localhost/api/*
- **Swagger UI**: http://localhost/swagger-ui
- **Health Check**: http://localhost/health
- **API Documentation**: http://localhost/v3/api-docs

## ⚙️ Configuration

### Environment Variables

You can customize the application by setting environment variables in `docker-compose.yml`:

```yaml
environment:
  - SPRING_PROFILES_ACTIVE=docker
  - JAVA_OPTS=-Xmx512m -Xms256m
  - JWT_SECRET=your-custom-secret-key
```

### Nginx Configuration

The nginx configuration (`nginx.conf`) includes:

- **Reverse Proxy**: Routes `/api/*` requests to the backend
- **Static File Serving**: Serves the React frontend
- **CORS Support**: Handles cross-origin requests
- **Rate Limiting**: Protects against abuse
- **Gzip Compression**: Optimizes response sizes
- **Security Headers**: Adds security-related HTTP headers

### Backend Configuration

The Docker configuration uses environment variables in `application.properties`:

- **Database Path**: Uses absolute path for SQLite
- **CORS**: Allows all origins for Docker deployment
- **Logging**: Reduced verbosity for production
- **Performance**: Optimized JPA settings

## 📊 Monitoring and Logs

### View Logs

```bash
# View all logs
docker compose logs -f

# View specific service logs
docker compose logs -f survey-app

# View nginx logs
docker compose exec survey-app tail -f /var/log/nginx/access.log
docker compose exec survey-app tail -f /var/log/nginx/error.log
```

### Health Checks

The application includes health checks that monitor:

- Nginx server availability
- Backend application health
- Database connectivity

### Metrics

Access application metrics at:
- http://localhost/actuator/metrics (requires authentication)

## 🔧 Development

### Rebuilding After Changes

```bash
# Enable bake for better build performance (optional)
export COMPOSE_BAKE=true

# Rebuild and restart
docker compose down
docker compose build --no-cache
docker compose up -d
```

### Debugging

```bash
# Access the container shell
docker compose exec survey-app sh

# Check running processes
docker compose exec survey-app ps aux

# Check nginx configuration
docker compose exec survey-app nginx -t
```

## 🗄️ Database

### Persistence

The SQLite database is mounted as a volume to ensure data persistence:

```yaml
volumes:
  - ./survey.db:/app/backend/survey.db
```

### Backup

To backup the database:

```bash
# Stop the application
docker compose down

# Copy the database file
cp survey.db survey.db.backup

# Restart the application
docker compose up -d
```

## 🔒 Security

### Security Features

- **Non-root User**: Application runs as non-root user
- **Security Headers**: XSS protection, content type sniffing prevention
- **Rate Limiting**: API rate limiting to prevent abuse
- **CORS Configuration**: Proper CORS setup for cross-origin requests

### Production Considerations

For production deployment:

1. **Change JWT Secret**: Update the JWT secret via environment variable `JWT_SECRET`
2. **Use HTTPS**: Configure SSL/TLS certificates
3. **Database**: Consider using a production database (PostgreSQL, MySQL)
4. **Monitoring**: Add proper monitoring and alerting
5. **Backup Strategy**: Implement automated database backups

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

#### Nginx Issues

```bash
# Test nginx configuration
docker compose exec survey-app nginx -t

# Check nginx error logs
docker compose exec survey-app tail -f /var/log/nginx/error.log
```

#### Memory Issues

If the application runs out of memory, increase the JVM heap size:

```yaml
environment:
  - JAVA_OPTS=-Xmx1g -Xms512m
```

### Performance Optimization

1. **Increase JVM Memory**: Adjust `JAVA_OPTS` for your server capacity
2. **Nginx Caching**: Configure nginx caching for static assets
3. **Database Optimization**: Consider database indexing and query optimization
4. **Load Balancing**: Use multiple instances behind a load balancer

## 📝 API Documentation

The API documentation is available at:
- **Swagger UI**: http://localhost/swagger-ui
- **OpenAPI JSON**: http://localhost/v3/api-docs

## 🤝 Contributing

When making changes to the Docker setup:

1. Test the build process locally
2. Update documentation if needed
3. Ensure all environment variables are properly configured
4. Test the application in a clean environment

## 📄 License

This Docker setup is part of the Survey Application project. 