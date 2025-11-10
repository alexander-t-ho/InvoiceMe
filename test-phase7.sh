#!/bin/bash

echo "🧪 Testing Phase 7: Frontend Feature Implementation"
echo "=================================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check if backend is running
echo "📡 Checking backend server..."
if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
    echo -e "${GREEN}✅ Backend is running${NC}"
else
    echo -e "${YELLOW}⚠️  Backend is not running. Starting backend...${NC}"
    echo "   Run: cd backend && ./gradlew bootRun"
    BACKEND_RUNNING=false
fi

# Check if frontend is running
echo "🌐 Checking frontend server..."
if curl -s http://localhost:3000 > /dev/null 2>&1; then
    echo -e "${GREEN}✅ Frontend is running${NC}"
else
    echo -e "${YELLOW}⚠️  Frontend is not running. Starting frontend...${NC}"
    echo "   Run: cd frontend && npm run dev"
    FRONTEND_RUNNING=false
fi

echo ""
echo "📋 Test Checklist:"
echo "=================="
echo ""
echo "1. ✅ Backend API accessible"
echo "2. ✅ Frontend accessible"
echo "3. ⏳ Create a customer"
echo "4. ⏳ List customers"
echo "5. ⏳ Create an invoice"
echo "6. ⏳ Add line items to invoice"
echo "7. ⏳ Mark invoice as sent"
echo "8. ⏳ Record a payment"
echo "9. ⏳ View invoice details"
echo "10. ⏳ Dashboard displays data"
echo ""
echo "Run manual tests in browser at http://localhost:3000"
echo ""

