# Testing PRD: InvoiceMe — Testing Strategy & Quality Assurance

## Document Purpose

This PRD outlines the comprehensive testing strategy for InvoiceMe, covering unit tests, integration tests, and end-to-end testing. This document focuses on Phase 9 requirements.

**Related Documents**: 
- PRD-00-Master.md (overview)
- PRD-01-Backend.md (backend implementation)
- PRD-02-Frontend.md (frontend implementation)

---

## Testing Philosophy

### Testing Pyramid
```
        /\
       /E2E\          (Few, critical user journeys)
      /------\
     /Integration\    (API endpoints, component integration)
    /------------\
   /   Unit Tests  \  (Domain logic, utilities, components)
  /----------------\
```

### Testing Principles
1. **Test Behavior, Not Implementation**: Focus on what the code does, not how
2. **Test Isolation**: Each test should be independent
3. **Fast Feedback**: Unit tests should run quickly
4. **High Coverage**: Aim for 70%+ code coverage, 100% for critical paths
5. **Maintainable Tests**: Tests should be easy to read and maintain

---

## Backend Testing

### Testing Stack
- **Unit Testing**: JUnit 5
- **Mocking**: Mockito
- **Integration Testing**: Testcontainers (PostgreSQL)
- **Assertions**: AssertJ (fluent assertions)
- **Test Data**: Faker (for generating test data)

---

### Phase 9: Integration Testing & Quality Assurance

**Duration**: 3-4 days  
**Goal**: Ensure system reliability and correctness

---

### Unit Tests

#### Domain Entity Tests
**Location**: `src/test/java/com/invoiceme/domain/`

**Customer Entity Tests**:
- ✅ `validate()` - Validates email format and required fields
- ✅ `updateDetails()` - Updates customer information correctly
- ✅ `equals()` / `hashCode()` - Correctly compares customers

**Invoice Entity Tests**:
- ✅ `addLineItem()` - Adds line item and recalculates total
- ✅ `removeLineItem()` - Removes line item and recalculates total
- ✅ `calculateTotal()` - Calculates total correctly
- ✅ `markAsSent()` - Transitions from DRAFT to SENT
- ✅ `markAsSent()` - Throws exception if no line items
- ✅ `applyPayment()` - Applies payment and updates balance
- ✅ `applyPayment()` - Transitions to PAID when balance is zero
- ✅ `applyPayment()` - Throws exception if payment exceeds balance
- ✅ `calculateBalance()` - Calculates balance correctly
- ✅ `canBeEdited()` - Returns true only for DRAFT status
- ✅ State transition validations (DRAFT → SENT → PAID)

**Payment Entity Tests**:
- ✅ `validate()` - Validates amount > 0 and date not in future
- ✅ `validateAgainstInvoice()` - Validates payment doesn't exceed balance

**LineItem Value Object Tests**:
- ✅ `total` calculation (quantity × unitPrice)
- ✅ Immutability

---

#### Command Handler Tests
**Location**: `src/test/java/com/invoiceme/application/`

**Test Structure**:
- Mock repository dependencies
- Test successful execution
- Test validation failures
- Test business rule violations
- Test exception handling

**Customer Command Tests**:
- ✅ `CreateCustomerHandler` - Creates customer successfully
- ✅ `CreateCustomerHandler` - Throws exception for duplicate email
- ✅ `UpdateCustomerHandler` - Updates customer successfully
- ✅ `UpdateCustomerHandler` - Throws exception if customer not found
- ✅ `DeleteCustomerHandler` - Deletes customer successfully
- ✅ `DeleteCustomerHandler` - Throws exception if customer has invoices

**Invoice Command Tests**:
- ✅ `CreateInvoiceHandler` - Creates invoice in DRAFT status
- ✅ `CreateInvoiceHandler` - Throws exception if customer not found
- ✅ `UpdateInvoiceHandler` - Updates invoice successfully
- ✅ `UpdateInvoiceHandler` - Throws exception if invoice not in DRAFT
- ✅ `MarkInvoiceAsSentHandler` - Marks invoice as SENT
- ✅ `MarkInvoiceAsSentHandler` - Throws exception if no line items
- ✅ `AddLineItemHandler` - Adds line item successfully
- ✅ `AddLineItemHandler` - Throws exception if invoice not in DRAFT
- ✅ `RemoveLineItemHandler` - Removes line item successfully

**Payment Command Tests**:
- ✅ `RecordPaymentHandler` - Records payment successfully
- ✅ `RecordPaymentHandler` - Updates invoice balance correctly
- ✅ `RecordPaymentHandler` - Transitions invoice to PAID when balance is zero
- ✅ `RecordPaymentHandler` - Throws exception if payment exceeds balance
- ✅ `RecordPaymentHandler` - Throws exception if invoice not found

---

#### Query Handler Tests
**Location**: `src/test/java/com/invoiceme/application/`

**Test Structure**:
- Mock repository dependencies
- Test data retrieval
- Test pagination
- Test filtering
- Test not found scenarios

**Customer Query Tests**:
- ✅ `GetCustomerByIdHandler` - Returns customer successfully
- ✅ `GetCustomerByIdHandler` - Throws exception if not found
- ✅ `ListAllCustomersHandler` - Returns paginated customers
- ✅ `ListAllCustomersHandler` - Handles empty results

**Invoice Query Tests**:
- ✅ `GetInvoiceByIdHandler` - Returns invoice with line items and payments
- ✅ `ListInvoicesByStatusHandler` - Filters by status correctly
- ✅ `ListInvoicesByCustomerHandler` - Filters by customer correctly
- ✅ Pagination works correctly

**Payment Query Tests**:
- ✅ `GetPaymentByIdHandler` - Returns payment successfully
- ✅ `ListPaymentsByInvoiceHandler` - Returns all payments for invoice

---

### Integration Tests

#### Repository Integration Tests
**Location**: `src/test/java/com/invoiceme/infrastructure/persistence/`

**Setup**: Use Testcontainers with PostgreSQL

**Customer Repository Tests**:
- ✅ Save and retrieve customer
- ✅ Update customer
- ✅ Delete customer
- ✅ Find by email (unique constraint)
- ✅ Pagination queries

**Invoice Repository Tests**:
- ✅ Save invoice with line items (cascade)
- ✅ Retrieve invoice with line items (eager/lazy loading)
- ✅ Update invoice
- ✅ Find by status
- ✅ Find by customer ID
- ✅ Delete invoice (cascade deletes line items)

**Payment Repository Tests**:
- ✅ Save payment
- ✅ Retrieve payment
- ✅ Find payments by invoice ID

---

#### API Integration Tests
**Location**: `src/test/java/com/invoiceme/api/`

**Setup**: Use `@SpringBootTest` with `@AutoConfigureMockMvc` or `@WebMvcTest`

**Test Structure**:
- Test all HTTP methods (GET, POST, PUT, DELETE)
- Test request validation
- Test response status codes
- Test response body structure
- Test error responses

**Customer API Tests**:
- ✅ `POST /api/v1/customers` - 201 Created
- ✅ `POST /api/v1/customers` - 400 Bad Request (validation error)
- ✅ `GET /api/v1/customers/{id}` - 200 OK
- ✅ `GET /api/v1/customers/{id}` - 404 Not Found
- ✅ `PUT /api/v1/customers/{id}` - 200 OK
- ✅ `DELETE /api/v1/customers/{id}` - 204 No Content
- ✅ `GET /api/v1/customers` - 200 OK with pagination

**Invoice API Tests**:
- ✅ `POST /api/v1/invoices` - 201 Created
- ✅ `GET /api/v1/invoices/{id}` - 200 OK
- ✅ `PUT /api/v1/invoices/{id}` - 200 OK (only if DRAFT)
- ✅ `PUT /api/v1/invoices/{id}` - 400 Bad Request (if not DRAFT)
- ✅ `POST /api/v1/invoices/{id}/send` - 200 OK
- ✅ `POST /api/v1/invoices/{id}/line-items` - 200 OK
- ✅ `DELETE /api/v1/invoices/{id}/line-items/{lineItemId}` - 200 OK
- ✅ `GET /api/v1/invoices?status={status}` - 200 OK with filtering
- ✅ `GET /api/v1/invoices?customerId={id}` - 200 OK with filtering

**Payment API Tests**:
- ✅ `POST /api/v1/payments` - 201 Created
- ✅ `POST /api/v1/payments` - 400 Bad Request (exceeds balance)
- ✅ `GET /api/v1/payments/{id}` - 200 OK
- ✅ `GET /api/v1/invoices/{invoiceId}/payments` - 200 OK

---

#### End-to-End Integration Tests

**Location**: `src/test/java/com/invoiceme/integration/`

**Setup**: Use `@SpringBootTest` with Testcontainers (full application context)

**Critical Business Flow Tests**:

1. **Complete Customer Lifecycle**:
   - ✅ Create customer
   - ✅ Update customer
   - ✅ Delete customer (if no invoices)

2. **Complete Invoice Lifecycle**:
   - ✅ Create invoice (DRAFT)
   - ✅ Add line items
   - ✅ Update invoice (while DRAFT)
   - ✅ Mark invoice as SENT
   - ✅ Verify invoice cannot be edited after SENT
   - ✅ Record payment
   - ✅ Verify invoice transitions to PAID when balance is zero
   - ✅ Verify balance calculation is correct

3. **Payment Application Flow**:
   - ✅ Create invoice with line items
   - ✅ Mark invoice as SENT
   - ✅ Record partial payment
   - ✅ Verify balance is updated correctly
   - ✅ Record remaining payment
   - ✅ Verify invoice transitions to PAID
   - ✅ Verify multiple payments are tracked correctly

4. **Invoice Balance Calculation**:
   - ✅ Create invoice with multiple line items
   - ✅ Verify total is calculated correctly
   - ✅ Record payment
   - ✅ Verify balance = total - payment
   - ✅ Record another payment
   - ✅ Verify balance = total - sum of payments
   - ✅ Verify balance cannot go negative

5. **State Transition Validations**:
   - ✅ Cannot mark invoice as SENT without line items
   - ✅ Cannot edit invoice after SENT
   - ✅ Cannot add line items after SENT
   - ✅ Cannot record payment for DRAFT invoice (optional business rule)

---

### Authentication Tests

**Location**: `src/test/java/com/invoiceme/security/`

**Authentication Tests**:
- ✅ `POST /api/v1/auth/login` - 200 OK with JWT token
- ✅ `POST /api/v1/auth/login` - 401 Unauthorized (invalid credentials)
- ✅ Protected endpoints require authentication (401 without token)
- ✅ Protected endpoints work with valid token (200 OK)
- ✅ JWT token expiration handling

---

### Performance Tests

**Location**: `src/test/java/com/invoiceme/performance/` (optional)

**Performance Benchmarks**:
- ✅ API response times < 200ms for standard CRUD operations
- ✅ Query performance with large datasets
- ✅ Database query optimization verification

---

## Frontend Testing

### Testing Stack
- **Unit Testing**: Vitest
- **Component Testing**: React Testing Library
- **E2E Testing**: Playwright or Cypress (optional)
- **Mocking**: MSW (Mock Service Worker) for API mocking

---

### Component Unit Tests

**Location**: `frontend/__tests__/components/`

**UI Component Tests**:
- ✅ Button renders correctly
- ✅ Input handles user input
- ✅ Form validation displays errors
- ✅ Modal opens and closes
- ✅ Table displays data correctly
- ✅ Loading states display correctly

**Test Structure**:
```typescript
describe('CustomerForm', () => {
  it('should validate required fields', () => {
    // Test validation
  });
  
  it('should submit form with valid data', () => {
    // Test submission
  });
});
```

---

### Integration Tests

**Location**: `frontend/__tests__/integration/`

**Form Integration Tests**:
- ✅ Customer form creates customer successfully
- ✅ Customer form displays validation errors
- ✅ Invoice form creates invoice with line items
- ✅ Payment form records payment successfully

**API Integration Tests** (using MSW):
- ✅ API client makes correct requests
- ✅ API client handles errors correctly
- ✅ React Query hooks fetch data correctly
- ✅ Mutations update data correctly

---

### User Flow Tests (E2E - Optional)

**Location**: `frontend/e2e/` (if using Playwright/Cypress)

**Critical User Journeys**:
1. **Customer Management Flow**:
   - ✅ Navigate to customers page
   - ✅ Create new customer
   - ✅ Edit customer
   - ✅ Delete customer

2. **Invoice Creation Flow**:
   - ✅ Navigate to create invoice page
   - ✅ Select customer
   - ✅ Add multiple line items
   - ✅ Verify total calculation
   - ✅ Submit invoice
   - ✅ Verify invoice is created in DRAFT status

3. **Invoice Payment Flow**:
   - ✅ View invoice detail
   - ✅ Mark invoice as SENT
   - ✅ Record payment
   - ✅ Verify balance updates
   - ✅ Verify invoice transitions to PAID

4. **Authentication Flow**:
   - ✅ Login with valid credentials
   - ✅ Access protected pages
   - ✅ Logout
   - ✅ Verify redirect to login

---

## Test Coverage Requirements

### Minimum Coverage Targets
- **Domain Entities**: 100% (all business logic)
- **Command Handlers**: 90%+
- **Query Handlers**: 85%+
- **API Controllers**: 80%+
- **Frontend Components**: 70%+
- **Overall Backend**: 70%+
- **Overall Frontend**: 70%+

### Coverage Tools
- **Backend**: JaCoCo (Java Code Coverage)
- **Frontend**: Vitest coverage or Istanbul

---

## Test Data Management

### Test Data Generation
- Use Faker library for generating test data
- Create test fixtures for common scenarios
- Use builders for complex domain objects

### Test Database
- Use Testcontainers for integration tests (PostgreSQL)
- Use H2 in-memory database for faster unit tests (if acceptable)
- Reset database between tests (use `@Transactional` or `@DirtiesContext`)

---

## Continuous Integration

### CI Pipeline (GitHub Actions)

**Test Stages**:
1. **Lint & Format Check**: ESLint, Prettier, Checkstyle
2. **Backend Unit Tests**: Run all backend unit tests
3. **Backend Integration Tests**: Run integration tests with Testcontainers
4. **Frontend Unit Tests**: Run frontend unit tests
5. **Frontend Integration Tests**: Run frontend integration tests
6. **Code Coverage Report**: Generate and upload coverage reports
7. **E2E Tests** (optional): Run end-to-end tests

**Failure Criteria**:
- Any test failure
- Code coverage below minimum threshold
- Linting errors

---

## Test Maintenance

### Best Practices
1. **Keep Tests DRY**: Use test utilities and fixtures
2. **Clear Test Names**: Test names should describe what is being tested
3. **One Assertion Per Test**: Focus each test on one behavior
4. **Test Data Builders**: Use builders for complex test data
5. **Mock External Dependencies**: Don't test external services
6. **Fast Tests**: Keep unit tests fast (< 100ms each)
7. **Isolated Tests**: Tests should not depend on each other

### Test Documentation
- Document complex test scenarios
- Explain why certain edge cases are tested
- Keep test code as clean as production code

---

## Success Criteria

### Phase 9 Completion Criteria
- ✅ All unit tests pass (100% for domain, 90%+ for handlers)
- ✅ All integration tests pass
- ✅ All API integration tests pass
- ✅ Critical business flows are fully tested
- ✅ Code coverage meets minimum thresholds
- ✅ No critical bugs remain
- ✅ Performance benchmarks are met (< 200ms API latency)
- ✅ CI pipeline runs all tests successfully

---

## Test Execution

### Running Tests Locally

**Backend**:
```bash
# Run all tests
./gradlew test

# Run integration tests
./gradlew integrationTest

# Run with coverage
./gradlew test jacocoTestReport
```

**Frontend**:
```bash
# Run all tests
npm test

# Run with coverage
npm test -- --coverage

# Run in watch mode
npm test -- --watch
```

---

## Bug Tracking

### Bug Severity Levels
1. **Critical**: System crashes, data loss, security vulnerabilities
2. **High**: Major functionality broken, incorrect business logic
3. **Medium**: Minor functionality issues, UI/UX problems
4. **Low**: Cosmetic issues, minor improvements

### Bug Resolution
- All critical and high bugs must be fixed before Phase 9 completion
- Document all bugs found during testing
- Track bug fixes in version control

---

**Document Version**: 1.0  
**Last Updated**: [Current Date]  
**Author**: Development Team


