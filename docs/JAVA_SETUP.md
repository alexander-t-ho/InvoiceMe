# Java 17 Setup Guide

## Current Status

✅ **Domain Model**: Complete (13 files)
✅ **Tests**: Complete (29 test cases)
⚠️ **Java Version**: Java 11 detected (Java 17+ required)

## Installing Java 17

### Option 1: Using Homebrew (macOS - Recommended)

```bash
# Install Java 17
brew install openjdk@17

# Link it
sudo ln -sfn /opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-17.jdk

# Set JAVA_HOME (add to ~/.zshrc or ~/.bash_profile)
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
export PATH="$JAVA_HOME/bin:$PATH"

# Verify installation
java -version
# Should show: openjdk version "17.x.x"
```

### Option 2: Using SDKMAN (Cross-platform)

```bash
# Install SDKMAN
curl -s "https://get.sdkman.io" | bash

# Install Java 17
sdk install java 17.0.9-tem

# Use it
sdk use java 17.0.9-tem

# Verify
java -version
```

### Option 3: Download from Adoptium

1. Visit: https://adoptium.net/
2. Download OpenJDK 17 (LTS) for your platform
3. Install the package
4. Set JAVA_HOME environment variable

### Option 4: Using IntelliJ IDEA

If you're using IntelliJ IDEA:
1. File → Project Structure → Project
2. Set SDK to Java 17
3. IntelliJ will use its bundled JDK for running tests

## Verifying Java 17 Installation

After installing Java 17:

```bash
# Check Java version
java -version
# Should show: openjdk version "17" or "java version "17"

# Check JAVA_HOME
echo $JAVA_HOME
# Should point to Java 17 installation

# List available Java versions (macOS)
/usr/libexec/java_home -V
```

## Running Tests After Java 17 Installation

Once Java 17 is installed:

```bash
cd backend

# Run all domain tests
./gradlew test --tests "com.invoiceme.domain.*"

# Run specific test class
./gradlew test --tests "com.invoiceme.domain.customers.CustomerTest"

# Run with coverage
./gradlew test jacocoTestReport
```

## Expected Test Results

All 29 tests should pass:

```
✅ CustomerTest
  ✅ shouldCreateCustomerWithValidData
  ✅ shouldThrowExceptionWhenNameIsNull
  ✅ shouldThrowExceptionWhenNameIsEmpty
  ✅ shouldThrowExceptionWhenEmailIsInvalid
  ✅ shouldNormalizeEmailToLowerCase
  ✅ shouldUpdateCustomerDetails
  ✅ shouldHaveEqualCustomersWithSameId

✅ InvoiceTest
  ✅ shouldCreateInvoiceInDraftStatus
  ✅ shouldAddLineItem
  ✅ shouldRemoveLineItem
  ✅ shouldNotAddLineItemWhenNotDraft
  ✅ shouldMarkAsSentWhenHasLineItems
  ✅ shouldNotMarkAsSentWithoutLineItems
  ✅ shouldCalculateBalanceCorrectly
  ✅ shouldTransitionToPaidWhenBalanceIsZero
  ✅ shouldNotAllowPaymentExceedingBalance

✅ LineItemTest
  ✅ shouldCreateLineItemWithValidData
  ✅ shouldCalculateTotalCorrectly
  ✅ shouldThrowExceptionWhenDescriptionIsNull
  ✅ shouldThrowExceptionWhenQuantityIsZero
  ✅ shouldThrowExceptionWhenQuantityIsNegative
  ✅ shouldAllowZeroUnitPrice
  ✅ shouldCreateLineItemWithSpecificId

✅ PaymentTest
  ✅ shouldCreatePaymentWithValidData
  ✅ shouldThrowExceptionWhenAmountIsZero
  ✅ shouldThrowExceptionWhenAmountIsNegative
  ✅ shouldThrowExceptionWhenPaymentDateIsInFuture
  ✅ shouldValidateAgainstInvoice
  ✅ shouldThrowExceptionWhenPaymentExceedsInvoiceBalance

BUILD SUCCESSFUL in Xs
29 tests completed, 29 passed
```

## Troubleshooting

### Issue: "java: command not found"
**Solution**: Make sure Java 17 is in your PATH

### Issue: Still using Java 11
**Solution**: 
```bash
# Check which Java is being used
which java

# Update PATH to use Java 17
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
export PATH="$JAVA_HOME/bin:$PATH"
```

### Issue: Gradle still uses Java 11
**Solution**: Set JAVA_HOME before running Gradle:
```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
./gradlew test
```

## Quick Verification Script

Run the verification script to check your setup:

```bash
cd backend
./verify-domain.sh
```

This will show:
- Domain files count
- Test files count
- Current Java version
- Instructions for running tests


