#!/bin/bash

# Survey Application Setup Script
# This script helps you set up and run the Survey Application

echo "🚀 Survey Application Setup"
echo "=========================="

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Please install Java 17 or higher."
    exit 1
fi

# Check if Node.js is installed
if ! command -v node &> /dev/null; then
    echo "❌ Node.js is not installed. Please install Node.js 18 or higher."
    exit 1
fi

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed. Please install Maven 3.6 or higher."
    exit 1
fi

echo "✅ Prerequisites check passed"

# Build backend
echo "🔨 Building backend..."
cd backend
if ./mvnw clean install; then
    echo "✅ Backend built successfully"
else
    echo "❌ Backend build failed"
    exit 1
fi

# Install frontend dependencies
echo "📦 Installing frontend dependencies..."
cd ../frontend
if npm install; then
    echo "✅ Frontend dependencies installed"
else
    echo "❌ Frontend dependency installation failed"
    exit 1
fi

cd ..

echo ""
echo "🎉 Setup completed successfully!"
echo ""
echo "To run the application:"
echo ""
echo "1. Start the backend:"
echo "   cd backend"
echo "   ./mvnw spring-boot:run"
echo ""
echo "2. In a new terminal, start the frontend:"
echo "   cd frontend"
echo "   npm start"
echo ""
echo "3. Access the application:"
echo "   Frontend: http://localhost:3000"
echo "   Backend API: http://localhost:8080"
echo "   Swagger UI: http://localhost:8080/swagger-ui.html"
echo ""
echo "Default admin credentials:"
echo "   Username: admin"
echo "   Password: admin123"
echo ""
echo "Happy surveying! 📊" 