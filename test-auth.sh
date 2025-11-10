#!/bin/bash

# Test Authentication Flow
API_BASE="http://localhost:8081/api/v1"

echo "🧪 Testing Phase 8: Authentication & Authorization"
echo "=================================================="
echo ""

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Test 1: Register a new user
echo "1. Testing user registration..."
REGISTER_RESPONSE=$(curl -s -X POST "$API_BASE/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123"
  }')

if echo "$REGISTER_RESPONSE" | grep -q "testuser"; then
  echo -e "${GREEN}✅ User registered successfully${NC}"
elif echo "$REGISTER_RESPONSE" | grep -q "already exists"; then
  echo -e "${YELLOW}⚠️  User already exists (this is OK)${NC}"
else
  echo -e "${RED}❌ Registration failed${NC}"
  echo "Response: $REGISTER_RESPONSE"
  # Continue anyway - user might already exist
fi

echo ""

# Test 2: Login with valid credentials
echo "2. Testing login with valid credentials..."
LOGIN_RESPONSE=$(curl -s -X POST "$API_BASE/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }')

TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*' | cut -d'"' -f4)

if [ -n "$TOKEN" ]; then
  echo -e "${GREEN}✅ Login successful, token received${NC}"
  echo "Token: ${TOKEN:0:50}..."
else
  echo -e "${RED}❌ Login failed${NC}"
  echo "Response: $LOGIN_RESPONSE"
  exit 1
fi

echo ""

# Test 3: Access protected endpoint without token
echo "3. Testing protected endpoint without token (should fail)..."
PROTECTED_RESPONSE=$(curl -s -w "\n%{http_code}" "$API_BASE/customers" 2>&1)
HTTP_CODE=$(echo "$PROTECTED_RESPONSE" | tail -1)

if [ "$HTTP_CODE" = "401" ] || [ "$HTTP_CODE" = "403" ]; then
  echo -e "${GREEN}✅ Protected endpoint correctly requires authentication (HTTP $HTTP_CODE)${NC}"
else
  echo -e "${YELLOW}⚠️  Unexpected response code: $HTTP_CODE${NC}"
fi

echo ""

# Test 4: Access protected endpoint with token
echo "4. Testing protected endpoint with token (should succeed)..."
AUTH_RESPONSE=$(curl -s -w "\n%{http_code}" "$API_BASE/customers" \
  -H "Authorization: Bearer $TOKEN" 2>&1)
AUTH_HTTP_CODE=$(echo "$AUTH_RESPONSE" | tail -1)

if [ "$AUTH_HTTP_CODE" = "200" ]; then
  echo -e "${GREEN}✅ Protected endpoint accessible with valid token${NC}"
else
  echo -e "${RED}❌ Failed to access protected endpoint with token (HTTP $AUTH_HTTP_CODE)${NC}"
  echo "Response: $AUTH_RESPONSE"
fi

echo ""

# Test 5: Login with invalid credentials
echo "5. Testing login with invalid credentials (should fail)..."
INVALID_LOGIN=$(curl -s -w "\n%{http_code}" -X POST "$API_BASE/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "wrongpassword"
  }' 2>&1)
INVALID_HTTP_CODE=$(echo "$INVALID_LOGIN" | tail -1)

if [ "$INVALID_HTTP_CODE" = "400" ] || [ "$INVALID_HTTP_CODE" = "401" ]; then
  echo -e "${GREEN}✅ Invalid credentials correctly rejected (HTTP $INVALID_HTTP_CODE)${NC}"
else
  echo -e "${YELLOW}⚠️  Unexpected response code: $INVALID_HTTP_CODE${NC}"
fi

echo ""
echo "✅ Authentication tests completed!"
echo ""
echo "Next: Test the frontend at http://localhost:3001"
echo "  1. Navigate to http://localhost:3001"
echo "  2. You should be redirected to /login"
echo "  3. Login with:"
echo "     Username: testuser"
echo "     Password: password123"
echo "  4. Verify you can access protected pages"

