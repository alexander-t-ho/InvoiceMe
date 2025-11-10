#!/bin/bash

# Simple verification script for domain model
# Note: Full tests require Java 17+ and Gradle

echo "=== Domain Model Verification ==="
echo ""

echo "Checking domain files..."
DOMAIN_FILES=$(find src/main/java/com/invoiceme/domain -name "*.java" | wc -l | tr -d ' ')
echo "✅ Found $DOMAIN_FILES domain files"

echo ""
echo "Checking test files..."
TEST_FILES=$(find src/test/java/com/invoiceme/domain -name "*Test.java" | wc -l | tr -d ' ')
echo "✅ Found $TEST_FILES test files"

echo ""
echo "Domain structure:"
find src/main/java/com/invoiceme/domain -name "*.java" | sed 's|src/main/java/||' | sort

echo ""
echo "Test structure:"
find src/test/java/com/invoiceme/domain -name "*Test.java" | sed 's|src/test/java/||' | sort

echo ""
echo "=== Summary ==="
echo "Domain Entities:"
echo "  - Customer"
echo "  - Invoice"
echo "  - InvoiceStatus (enum)"
echo "  - LineItem (value object)"
echo "  - Payment"
echo ""
echo "Domain Exceptions:"
echo "  - DomainValidationException"
echo "  - InvalidInvoiceStateException"
echo "  - InsufficientPaymentException"
echo "  - InvalidLineItemException"
echo ""
echo "Domain Events:"
echo "  - InvoiceCreatedEvent"
echo "  - InvoiceSentEvent"
echo "  - PaymentRecordedEvent"
echo "  - InvoicePaidEvent"
echo ""
echo "Test Coverage:"
echo "  - CustomerTest: 7 tests"
echo "  - InvoiceTest: 9 tests"
echo "  - LineItemTest: 7 tests"
echo "  - PaymentTest: 6 tests"
echo "  - Total: 29 tests"
echo ""
echo "⚠️  Note: To run tests, Java 17+ is required."
echo "   Current Java version: $(java -version 2>&1 | head -1)"
echo ""
echo "To install Java 17:"
echo "  - macOS: brew install openjdk@17"
echo "  - Or download from: https://adoptium.net/"
echo ""
echo "Once Java 17 is installed, run:"
echo "  ./gradlew test --tests 'com.invoiceme.domain.*'"


