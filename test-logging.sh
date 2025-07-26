#!/bin/bash

# Test script to demonstrate logging functionality

echo "🧪 Testing Logging Configuration"
echo "================================"

# Start the container if not running
if ! docker compose ps | grep -q "survey-app.*Up"; then
    echo "Starting survey-app container..."
    docker compose up -d
    sleep 15
fi

echo ""
echo "📊 Current Log Levels:"
echo "====================="
docker compose exec survey-app env | grep LOG_LEVEL

echo ""
echo "🔍 Testing Different Log Levels:"
echo "================================"

# Test with INFO level (default)
echo "1. Testing with INFO level (default)..."
curl -s -X POST http://localhost/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' > /dev/null

echo "2. Testing with DEBUG level..."
docker compose down
docker compose up -d -e LOG_LEVEL_CONTROLLER=DEBUG -e LOG_LEVEL_SERVICE=DEBUG
sleep 10

curl -s -X POST http://localhost/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' > /dev/null

echo "3. Testing with WARN level..."
docker compose down
docker compose up -d -e LOG_LEVEL_CONTROLLER=WARN -e LOG_LEVEL_SERVICE=WARN
sleep 10

curl -s -X POST http://localhost/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' > /dev/null

echo ""
echo "📋 Log Analysis:"
echo "================"

echo "Recent logs with different levels:"
docker compose logs --tail 20 survey-app

echo ""
echo "🔍 Log Level Distribution:"
echo "========================="
docker compose logs survey-app | grep -o "ERROR\|WARN\|INFO\|DEBUG" | sort | uniq -c

echo ""
echo "🚨 Error and Warning Logs:"
echo "=========================="
docker compose logs survey-app | grep -E "ERROR|WARN" | tail -5

echo ""
echo "✅ Authentication Logs:"
echo "======================"
docker compose logs survey-app | grep -E "Login|authentication" | tail -5

echo ""
echo "📁 Log File Information:"
echo "======================="
if docker compose exec survey-app test -f /app/backend/logs/survey-app.log; then
    echo "Log file exists:"
    docker compose exec survey-app ls -la /app/backend/logs/
    echo ""
    echo "Recent log file entries:"
    docker compose exec survey-app tail -5 /app/backend/logs/survey-app.log
else
    echo "No log file found (logs are going to console only)"
fi

echo ""
echo "🎛️  Logging Configuration Examples:"
echo "==================================="
echo ""
echo "For Development (Verbose):"
echo "  LOG_LEVEL_ROOT=DEBUG"
echo "  LOG_LEVEL_SURVEY=DEBUG"
echo "  LOG_LEVEL_CONTROLLER=DEBUG"
echo "  LOG_LEVEL_SERVICE=DEBUG"
echo "  LOG_LEVEL_HIBERNATE=DEBUG"
echo ""
echo "For Production (Minimal):"
echo "  LOG_LEVEL_ROOT=WARN"
echo "  LOG_LEVEL_SURVEY=INFO"
echo "  LOG_LEVEL_CONTROLLER=WARN"
echo "  LOG_LEVEL_SERVICE=INFO"
echo "  LOG_LEVEL_HIBERNATE=WARN"
echo ""
echo "For Debugging Authentication:"
echo "  LOG_LEVEL_SECURITY=DEBUG"
echo "  LOG_LEVEL_SERVICE=DEBUG"
echo ""
echo "For Debugging Database:"
echo "  LOG_LEVEL_HIBERNATE=DEBUG"
echo "  LOG_LEVEL_REPOSITORY=DEBUG" 