#!/bin/bash

# Test API endpoints for Phase 7
API_BASE="http://localhost:8081/api/v1"

echo "🧪 Testing API Endpoints"
echo "========================"
echo ""

# Test 1: Create Customer
echo "1. Creating a test customer..."
CUSTOMER_RESPONSE=$(curl -s -X POST "$API_BASE/customers" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Customer",
    "email": "test@example.com",
    "address": "123 Test St"
  }')

CUSTOMER_ID=$(echo $CUSTOMER_RESPONSE | grep -o '"id":"[^"]*' | cut -d'"' -f4)

if [ -n "$CUSTOMER_ID" ]; then
  echo "✅ Customer created: $CUSTOMER_ID"
else
  echo "❌ Failed to create customer"
  echo "Response: $CUSTOMER_RESPONSE"
  exit 1
fi

echo ""

# Test 2: List Customers
echo "2. Listing customers..."
CUSTOMERS_LIST=$(curl -s "$API_BASE/customers?page=0&size=10")
CUSTOMER_COUNT=$(echo $CUSTOMERS_LIST | grep -o '"totalElements":[0-9]*' | cut -d':' -f2)
echo "✅ Found $CUSTOMER_COUNT customers"

echo ""

# Test 3: Create Invoice
echo "3. Creating a test invoice..."
INVOICE_RESPONSE=$(curl -s -X POST "$API_BASE/invoices" \
  -H "Content-Type: application/json" \
  -d "{
    \"customerId\": \"$CUSTOMER_ID\",
    \"issueDate\": \"$(date +%Y-%m-%d)\",
    \"dueDate\": \"$(date -v+30d +%Y-%m-%d 2>/dev/null || date -d '+30 days' +%Y-%m-%d)\"
  }")

INVOICE_ID=$(echo $INVOICE_RESPONSE | grep -o '"id":"[^"]*' | cut -d'"' -f4)

if [ -n "$INVOICE_ID" ]; then
  echo "✅ Invoice created: $INVOICE_ID"
else
  echo "❌ Failed to create invoice"
  echo "Response: $INVOICE_RESPONSE"
  exit 1
fi

echo ""

# Test 4: Add Line Item
echo "4. Adding line item to invoice..."
LINE_ITEM_RESPONSE=$(curl -s -X POST "$API_BASE/invoices/$INVOICE_ID/line-items" \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Test Product",
    "quantity": 2,
    "unitPrice": 100.00
  }')

if echo "$LINE_ITEM_RESPONSE" | grep -q "Test Product"; then
  echo "✅ Line item added successfully"
else
  echo "❌ Failed to add line item"
  echo "Response: $LINE_ITEM_RESPONSE"
fi

echo ""

# Test 5: Mark Invoice as Sent
echo "5. Marking invoice as sent..."
SENT_RESPONSE=$(curl -s -X POST "$API_BASE/invoices/$INVOICE_ID/mark-as-sent" \
  -H "Content-Type: application/json")

if echo "$SENT_RESPONSE" | grep -q "SENT"; then
  echo "✅ Invoice marked as sent"
else
  echo "❌ Failed to mark invoice as sent"
  echo "Response: $SENT_RESPONSE"
fi

echo ""

# Test 6: Record Payment
echo "6. Recording a payment..."
PAYMENT_RESPONSE=$(curl -s -X POST "$API_BASE/payments" \
  -H "Content-Type: application/json" \
  -d "{
    \"invoiceId\": \"$INVOICE_ID\",
    \"amount\": 150.00,
    \"paymentDate\": \"$(date +%Y-%m-%d)\",
    \"paymentMethod\": \"BANK_TRANSFER\"
  }")

PAYMENT_ID=$(echo $PAYMENT_RESPONSE | grep -o '"id":"[^"]*' | cut -d'"' -f4)

if [ -n "$PAYMENT_ID" ]; then
  echo "✅ Payment recorded: $PAYMENT_ID"
else
  echo "❌ Failed to record payment"
  echo "Response: $PAYMENT_RESPONSE"
fi

echo ""

# Test 7: Get Invoice Details
echo "7. Getting invoice details..."
INVOICE_DETAILS=$(curl -s "$API_BASE/invoices/$INVOICE_ID")
BALANCE=$(echo $INVOICE_DETAILS | grep -o '"balance":[0-9.]*' | cut -d':' -f2)
TOTAL=$(echo $INVOICE_DETAILS | grep -o '"totalAmount":[0-9.]*' | cut -d':' -f2)

if [ -n "$BALANCE" ]; then
  echo "✅ Invoice details retrieved"
  echo "   Total: \$$TOTAL, Balance: \$$BALANCE"
else
  echo "❌ Failed to get invoice details"
fi

echo ""
echo "✅ API endpoint tests completed!"
echo ""
echo "Test Data Created:"
echo "  - Customer ID: $CUSTOMER_ID"
echo "  - Invoice ID: $INVOICE_ID"
echo "  - Payment ID: $PAYMENT_ID"
echo ""
echo "You can now test the frontend at http://localhost:3000"

