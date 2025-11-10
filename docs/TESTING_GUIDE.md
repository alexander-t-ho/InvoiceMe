# Domain Model Testing Guide

## Quick Start

### Option 1: Using Gradle Wrapper (Recommended)

If you have Java 17+ installed:

```bash
cd backend

# First time: Generate Gradle wrapper (if not already present)
# This will download Gradle automatically
./gradlew wrapper

# Run all domain tests
./gradlew test --tests "com.invoiceme.domain.*"

# Run with coverage
./gradlew test jacocoTestReport
```

### Option 2: Using IDE

1. **IntelliJ IDEA**:
   - Right-click on `backend/src/test/java/com/invoiceme/domain`
   - Select "Run 'All Tests'"
   - Or run individual test classes

2. **VS Code**:
   - Install "Extension Pack for Java"
   - Open test file
   - Click "Run Test" above test methods

3. **Eclipse**:
   - Right-click on test class
   - Run As → JUnit Test

---

## Test Execution Commands

### Run All Domain Tests
```bash
./gradlew test --tests "com.invoiceme.domain.*"
```

### Run Specific Test Classes
```bash
# Customer tests
./gradlew test --tests "com.invoiceme.domain.customers.CustomerTest"

# Invoice tests
./gradlew test --tests "com.invoiceme.domain.invoices.InvoiceTest"

# LineItem tests
./gradlew test --tests "com.invoiceme.domain.invoices.LineItemTest"

# Payment tests
./gradlew test --tests "com.invoiceme.domain.payments.PaymentTest"
```

### Run Specific Test Method
```bash
./gradlew test --tests "com.invoiceme.domain.customers.CustomerTest.shouldCreateCustomerWithValidData"
```

### Generate Test Coverage Report
```bash
./gradlew test jacocoTestReport
# Open: build/reports/jacoco/test/html/index.html
```

---

## Expected Test Results

All 29 tests should pass:

```
✅ CustomerTest (7 tests)
  ✅ shouldCreateCustomerWithValidData
  ✅ shouldThrowExceptionWhenNameIsNull
  ✅ shouldThrowExceptionWhenNameIsEmpty
  ✅ shouldThrowExceptionWhenEmailIsInvalid
  ✅ shouldNormalizeEmailToLowerCase
  ✅ shouldUpdateCustomerDetails
  ✅ shouldHaveEqualCustomersWithSameId

✅ InvoiceTest (9 tests)
  ✅ shouldCreateInvoiceInDraftStatus
  ✅ shouldAddLineItem
  ✅ shouldRemoveLineItem
  ✅ shouldNotAddLineItemWhenNotDraft
  ✅ shouldMarkAsSentWhenHasLineItems
  ✅ shouldNotMarkAsSentWithoutLineItems
  ✅ shouldCalculateBalanceCorrectly
  ✅ shouldTransitionToPaidWhenBalanceIsZero
  ✅ shouldNotAllowPaymentExceedingBalance

✅ LineItemTest (7 tests)
  ✅ shouldCreateLineItemWithValidData
  ✅ shouldCalculateTotalCorrectly
  ✅ shouldThrowExceptionWhenDescriptionIsNull
  ✅ shouldThrowExceptionWhenQuantityIsZero
  ✅ shouldThrowExceptionWhenQuantityIsNegative
  ✅ shouldAllowZeroUnitPrice
  ✅ shouldCreateLineItemWithSpecificId

✅ PaymentTest (6 tests)
  ✅ shouldCreatePaymentWithValidData
  ✅ shouldThrowExceptionWhenAmountIsZero
  ✅ shouldThrowExceptionWhenAmountIsNegative
  ✅ shouldThrowExceptionWhenPaymentDateIsInFuture
  ✅ shouldValidateAgainstInvoice
  ✅ shouldThrowExceptionWhenPaymentExceedsInvoiceBalance
```

---

## Troubleshooting

### Issue: Gradle wrapper not found
**Solution**: 
```bash
cd backend
gradle wrapper --gradle-version 8.5
# Or if gradle is not installed, download wrapper manually
```

### Issue: Java version mismatch
**Solution**: Ensure Java 17+ is installed
```bash
java -version  # Should show 17 or higher
```

### Issue: Tests fail with compilation errors
**Solution**: Clean and rebuild
```bash
./gradlew clean test
```

---

## Manual Verification Checklist

Before proceeding to Phase 3, verify:

- [ ] All 29 tests pass
- [ ] Customer validation works correctly
- [ ] Invoice lifecycle (DRAFT → SENT → PAID) works
- [ ] Balance calculation is accurate
- [ ] Payment validation prevents invalid payments
- [ ] All business rules are enforced
- [ ] No framework dependencies in domain layer

---

## Next Steps After Testing

Once all tests pass:

1. ✅ **Phase 2 Complete**: Domain model verified
2. **Proceed to Phase 3**: CQRS Commands Implementation
   - Will use these domain entities
   - Will implement command handlers
   - Will add repository interfaces


