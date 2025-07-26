#!/bin/sh

# Function to handle shutdown gracefully
cleanup() {
    echo "Shutting down gracefully..."
    if [ ! -z "$BACKEND_PID" ]; then
        kill -TERM "$BACKEND_PID" 2>/dev/null
        wait "$BACKEND_PID"
    fi
    nginx -s quit
    exit 0
}

# Set up signal handlers
trap cleanup SIGTERM SIGINT

# Start the Spring Boot application first
echo "Starting Spring Boot application..."
cd /app/backend
java -jar survey-backend.jar &
BACKEND_PID=$!

# Wait for backend to be ready
echo "Waiting for backend to be ready..."
for i in $(seq 1 30); do
    if wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health 2>/dev/null; then
        echo "Backend is ready!"
        break
    fi
    echo "Waiting for backend... ($i/30)"
    sleep 2
done

# Start nginx in background after backend is ready
echo "Starting nginx..."
if [ -f "/start-nginx.sh" ]; then
    /start-nginx.sh &
    NGINX_PID=$!
else
    echo "Warning: /start-nginx.sh not found, starting nginx directly..."
    nginx -g "daemon off;" &
    NGINX_PID=$!
fi

# Wait a moment for nginx to start
sleep 2

# Wait for both processes
wait $BACKEND_PID $NGINX_PID 