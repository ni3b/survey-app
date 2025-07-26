#!/bin/bash

# Test script for user login and survey authentication functionality

BASE_URL="http://localhost:80/api"
echo "Testing User Login and Survey Authentication Functionality"
echo "=========================================================="

# Test 1: User Login (assuming user exists)
echo -e "\n2. Testing User Login..."
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }')

echo "Login Response: $LOGIN_RESPONSE"

# Extract token from login response
TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

if [ -n "$TOKEN" ]; then
    echo "Token extracted: ${TOKEN:0:20}..."
    
    # Test 3: Get Active Surveys
    echo -e "\n3. Testing Get Active Surveys..."
    SURVEYS_RESPONSE=$(curl -s -X GET "$BASE_URL/surveys/active" \
      -H "Authorization: Bearer $TOKEN")
    
    echo "Active Surveys Response: $SURVEYS_RESPONSE"
    
    # Test 4: Get Survey Details (should show authentication requirements)
    echo -e "\n4. Testing Get Survey Details..."
    SURVEY_DETAILS=$(curl -s -X GET "$BASE_URL/surveys/2" \
      -H "Authorization: Bearer $TOKEN")
    
    echo "Survey Details Response: $SURVEY_DETAILS"
    
    # Test 5: Submit Response to Survey (should work with authentication)
    echo -e "\n5. Testing Submit Response (with authentication)..."
    SUBMIT_RESPONSE=$(curl -s -X POST "$BASE_URL/responses?questionId=6" \
      -H "Content-Type: application/json" \
      -H "Authorization: Bearer $TOKEN" \
      -d '{
        "text": "This is a test response from authenticated user"
      }')
    
    echo "Submit Response: $SUBMIT_RESPONSE"
    
else
    echo "Failed to extract token from login response"
fi

# Test 6: Submit Response without authentication (should fail for surveys requiring auth)
echo -e "\n6. Testing Submit Response without authentication..."
SUBMIT_NO_AUTH=$(curl -s -X POST "$BASE_URL/responses?questionId=6" \
  -H "Content-Type: application/json" \
  -d '{
    "text": "This should fail without authentication"
  }')

echo "Submit Response (no auth): $SUBMIT_NO_AUTH"



echo -e "\n=========================================================="
echo "Test completed!" 