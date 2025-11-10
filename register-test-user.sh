#!/bin/bash

# Script to register a test user for authentication testing

API_BASE="http://localhost:8081/api/v1"

echo "🔐 Registering Test User"
echo "========================"
echo ""

# Check if backend is running
if ! curl -s http://localhost:8081/actuator/health > /dev/null 2>&1; then
    echo "❌ Backend is not running!"
    echo ""
    echo "Please start the backend first:"
    echo "  cd backend"
    echo "  export JAVA_HOME=\$(brew --prefix openjdk@17)"
    echo "  export PATH=\"\$JAVA_HOME/bin:\$PATH\""
    echo "  ./gradlew bootRun"
    echo ""
    exit 1
fi

echo "✅ Backend is running"
echo ""

# Register user
echo "Registering user..."
RESPONSE=$(curl -s -X POST "$API_BASE/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123"
  }')

if echo "$RESPONSE" | grep -q "testuser"; then
    echo "✅ User registered successfully!"
    echo ""
    echo "📋 Login Credentials:"
    echo "   Username: testuser"
    echo "   Password: password123"
    echo ""
    echo "🌐 Now you can login at: http://localhost:3001"
elif echo "$RESPONSE" | grep -q "already exists"; then
    echo "✅ User already exists!"
    echo ""
    echo "📋 Login Credentials:"
    echo "   Username: testuser"
    echo "   Password: password123"
    echo ""
    echo "🌐 You can login at: http://localhost:3001"
else
    echo "❌ Registration failed"
    echo "Response: $RESPONSE"
    exit 1
fi

