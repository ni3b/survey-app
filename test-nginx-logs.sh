#!/bin/bash

# Test script to verify nginx access logs are visible in docker logs

echo "Testing nginx access logs visibility..."

# Start the container if not running
if ! docker-compose ps | grep -q "survey-app.*Up"; then
    echo "Starting survey-app container..."
    docker-compose up -d
    sleep 10
fi

# Get container name
CONTAINER_NAME=$(docker-compose ps -q survey-app)

echo "Container ID: $CONTAINER_NAME"

# Make some test requests to generate access logs
echo "Making test requests to generate access logs..."

# Test homepage
curl -s http://localhost/ > /dev/null
echo "Requested homepage"

# Test API endpoint
curl -s http://localhost/api/health > /dev/null
echo "Requested health endpoint"

# Test non-existent page (should generate 404)
curl -s http://localhost/nonexistent > /dev/null
echo "Requested non-existent page"

# Wait a moment for logs to be written
sleep 2

echo ""
echo "=== Recent Docker Logs ==="
docker logs --tail 20 $CONTAINER_NAME

echo ""
echo "=== Checking for nginx access logs ==="
if docker logs $CONTAINER_NAME 2>&1 | grep -q "nginx.*access"; then
    echo "✓ Nginx access logs found in docker logs"
else
    echo "✗ Nginx access logs not found in docker logs"
    echo ""
    echo "=== All recent logs ==="
    docker logs --tail 50 $CONTAINER_NAME
fi

echo ""
echo "=== Testing with docker-compose logs ==="
docker-compose logs --tail 20 survey-app 