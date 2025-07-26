# Multi-stage Dockerfile for Survey Application
# Stage 1: Build Frontend
FROM node:18-alpine AS frontend-builder

WORKDIR /app/frontend

# Copy package files
COPY frontend/package*.json ./

# Install dependencies
RUN npm ci --only=production

# Copy source code
COPY frontend/ ./

# Build the application
RUN npm run build

# Stage 2: Build Backend
FROM maven:3.9.6-eclipse-temurin-17-alpine AS backend-builder

WORKDIR /app/backend

# Copy pom.xml first for better caching
COPY backend/pom.xml ./

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY backend/src ./src

# Build the application
RUN mvn clean package -DskipTests

# Stage 3: Final Runtime Image
FROM nginx:alpine

# Install Java runtime
RUN apk add --no-cache openjdk17-jre

# Create application directories
RUN mkdir -p /app/backend /app/frontend /app/backend/logs /var/log/nginx

# Copy built frontend from Stage 1
COPY --from=frontend-builder /app/frontend/build /app/frontend

# Copy built backend JAR from Stage 2
COPY --from=backend-builder /app/backend/target/survey-backend-0.0.1-SNAPSHOT.jar /app/backend/survey-backend.jar

# Install wget for health checks
RUN apk add --no-cache wget

# Copy nginx configuration template and startup script
COPY nginx.conf.template /etc/nginx/nginx.conf.template
COPY start-nginx.sh /start-nginx.sh
RUN chmod +x /start-nginx.sh && \
    apk add --no-cache bash gettext && \
    # Remove default nginx config to avoid conflicts
    rm -f /etc/nginx/conf.d/default.conf

# Copy main startup script
COPY start.sh /start.sh
RUN chmod +x /start.sh

# Create non-root user for security
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# Create nginx directories and set proper permissions
RUN mkdir -p /var/log/nginx /var/cache/nginx /var/run /tmp && \
    chown -R appuser:appgroup /var/log/nginx /var/cache/nginx /var/run /tmp && \
    chmod -R 755 /var/log/nginx /var/cache/nginx /var/run /tmp

# Set permissions for backend logs directory
RUN chown -R appuser:appgroup /app/backend/logs && \
    chmod -R 755 /app/backend/logs

# Set ownership for application directories
RUN chown -R appuser:appgroup /app /etc/nginx

# Switch to non-root user
USER appuser

# Expose port 80
EXPOSE 80

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Start the application
CMD ["/start.sh"] 