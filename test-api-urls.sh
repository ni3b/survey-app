#!/bin/bash

# Test script to verify API URLs are working correctly

echo "Testing API URL configuration..."

# Start the container if not running
if ! docker-compose ps | grep -q "survey-app.*Up"; then
    echo "Starting survey-app container..."
    docker-compose up -d
    sleep 10
fi

echo ""
echo "=== Testing Direct Backend Access (port 8080) ==="
echo "Testing: http://localhost:8080/api/auth/login"
if curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/auth/login | grep -q "405"; then
    echo "✓ Backend is accessible on port 8080 (405 Method Not Allowed is expected for GET on POST endpoint)"
else
    echo "✗ Backend not accessible on port 8080"
fi

echo ""
echo "=== Testing Nginx Proxy Access (port 80) ==="
echo "Testing: http://localhost/api/auth/login"
if curl -s -o /dev/null -w "%{http_code}" http://localhost/api/auth/login | grep -q "405"; then
    echo "✓ Nginx proxy is working correctly (405 Method Not Allowed is expected for GET on POST endpoint)"
else
    echo "✗ Nginx proxy not working correctly"
fi

echo ""
echo "=== Testing Frontend Access ==="
echo "Testing: http://localhost/"
if curl -s -o /dev/null -w "%{http_code}" http://localhost/ | grep -q "200"; then
    echo "✓ Frontend is accessible via nginx proxy"
else
    echo "✗ Frontend not accessible via nginx proxy"
fi

echo ""
echo "=== Testing Health Endpoint ==="
echo "Testing: http://localhost/health"
if curl -s http://localhost/health | grep -q "status"; then
    echo "✓ Health endpoint is working via nginx proxy"
else
    echo "✗ Health endpoint not working via nginx proxy"
fi

echo ""
echo "=== Summary ==="
echo "The frontend should now make API calls to:"
echo "  - http://localhost/api/* (via nginx proxy) ✅"
echo "Instead of:"
echo "  - http://localhost:8080/api/* (direct backend) ❌"
echo ""
echo "This ensures all requests go through the nginx reverse proxy"
echo "and are properly logged in the Docker logs." 