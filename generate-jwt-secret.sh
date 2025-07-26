#!/bin/bash

# Generate a secure JWT secret for production use
# This script generates a 64-character random string suitable for HS512 algorithm

echo "Generating secure JWT secret for HS512 algorithm..."
echo ""

# Generate a 64-character random string using base64 encoding
JWT_SECRET=$(openssl rand -base64 48 | tr -d "=+/" | cut -c1-64)

echo "Generated JWT Secret:"
echo "$JWT_SECRET"
echo ""
echo "Add this to your environment variables:"
echo "export JWT_SECRET=\"$JWT_SECRET\""
echo ""
echo "Or add to your docker-compose.yml:"
echo "  - JWT_SECRET=$JWT_SECRET"
echo ""
echo "Note: Keep this secret secure and do not commit it to version control!" 