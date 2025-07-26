#!/bin/bash

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}🚀 Survey Application Docker Build and Run Script${NC}"
echo "=================================================="

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo -e "${RED}❌ Docker is not installed. Please install Docker first.${NC}"
    exit 1
fi

# Check if Docker Compose is installed
if ! command -v docker compose &> /dev/null; then
    echo -e "${RED}❌ Docker Compose is not installed. Please install Docker Compose first.${NC}"
    exit 1
fi

# Create logs directory if it doesn't exist
mkdir -p logs

# Enable bake for better build performance
export COMPOSE_BAKE=true

echo -e "${YELLOW}📦 Building Docker image with bake optimization...${NC}"
docker compose build

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Docker image built successfully!${NC}"
else
    echo -e "${RED}❌ Failed to build Docker image${NC}"
    exit 1
fi

echo -e "${YELLOW}🚀 Starting the application...${NC}"
docker compose up -d

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Application started successfully!${NC}"
    echo ""
    echo -e "${GREEN}🌐 Application is running at:${NC}"
    echo -e "   Frontend: ${GREEN}http://localhost${NC}"
    echo -e "   API: ${GREEN}http://localhost/api${NC}"
    echo -e "   Swagger UI: ${GREEN}http://localhost/swagger-ui${NC}"
    echo -e "   Health Check: ${GREEN}http://localhost/health${NC}"
    echo ""
    echo -e "${YELLOW}📋 Useful commands:${NC}"
    echo -e "   View logs: ${GREEN}docker compose logs -f${NC}"
    echo -e "   Stop app: ${GREEN}docker compose down${NC}"
    echo -e "   Restart app: ${GREEN}docker compose restart${NC}"
    echo ""
    echo -e "${YELLOW}⏳ Waiting for application to be ready...${NC}"
    
    # Wait for application to be ready
    for i in {1..30}; do
        if curl -s http://localhost/health > /dev/null 2>&1; then
            echo -e "${GREEN}✅ Application is ready!${NC}"
            break
        fi
        echo -n "."
        sleep 2
    done
    
    echo ""
    echo -e "${GREEN}🎉 Survey application is now running!${NC}"
else
    echo -e "${RED}❌ Failed to start application${NC}"
    echo -e "${YELLOW}📋 Check logs with: docker compose logs${NC}"
    exit 1
fi 