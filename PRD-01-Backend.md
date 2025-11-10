# Backend PRD: InvoiceMe — Backend Implementation

## Document Purpose

This PRD details the backend implementation for InvoiceMe, covering domain modeling, CQRS implementation, REST API development, and authentication. This document focuses on Phases 2-5 and Phase 8 (backend portion).

**Related Documents**: 
- PRD-00-Master.md (overview and architecture)
- PRD-03-Testing.md (testing requirements)
- PRD-04-Infrastructure.md (deployment and infrastructure)

---

## Tech Stack

- **Framework**: Spring Boot 3.2+ (Java 17+)
- **Build Tool**: Maven or Gradle (Gradle recommended)
- **Database**: 
  - **Development/Testing**: H2 Database (in-memory)
  - **Production**: PostgreSQL 15+
- **ORM**: Spring Data JPA with Hibernate
- **Validation**: Jakarta Bean Validation
- **Security**: Spring Security with JWT
- **Testing**: JUnit 5, Mockito, Testcontainers
- **API Documentation**: SpringDoc OpenAPI (Swagger)

---

## Phase 2: Domain Model & Core Entities (DDD Foundation)

**Duration**: 2-3 days  
**Goal**: Implement rich domain models with business logic

### Domain Entities

#### Customer Domain Entity
**Location**: `com.invoiceme.domain.customers.Customer`

**Properties**:
- `id` (UUID, primary key)
- `name` (String, required)
- `email` (String, required, unique)
- `address` (String, optional)
- `createdAt` (LocalDateTime)
- `updatedAt` (LocalDateTime)

**Domain Methods**:
- `validate()` - Validates email format and required fields
- `updateDetails(String name, String email, String address)` - Updates customer information with validation
- `equals()` / `hashCode()` - Based on id

**Business Rules**:
- Email must be unique across all customers
- Email must be in valid format
- Name cannot be null or empty

---

#### Invoice Domain Entity
**Location**: `com.invoiceme.domain.invoices.Invoice`

**Properties**:
- `id` (UUID, primary key)
- `customerId` (UUID, required, foreign key)
- `status` (InvoiceStatus enum: DRAFT, SENT, PAID)
- `issueDate` (LocalDate, required)
- `dueDate` (LocalDate, optional)
- `lineItems` (List<LineItem>, value object)
- `payments` (List<Payment>, aggregate reference)
- `totalAmount` (BigDecimal, calculated)
- `balance` (BigDecimal, calculated)
- `createdAt` (LocalDateTime)
- `updatedAt` (LocalDateTime)

**Domain Methods**:
- `addLineItem(LineItem item)` - Adds a line item, recalculates total
- `removeLineItem(UUID lineItemId)` - Removes a line item, recalculates total
- `calculateTotal()` - Calculates total from all line items
- `markAsSent()` - Transitions from DRAFT to SENT (validates state)
- `applyPayment(Payment payment)` - Applies payment, updates balance, transitions to PAID if balance is zero
- `calculateBalance()` - Calculates remaining balance (totalAmount - sum of payments)
- `canBeEdited()` - Returns true if status is DRAFT
- `canBeSent()` - Returns true if status is DRAFT and has line items

**Business Rules**:
- Invoice must have at least one line item before being sent
- Invoice cannot be modified after being sent (only payments can be applied)
- Balance cannot be negative
- Payment amount cannot exceed remaining balance
- Status transitions: DRAFT → SENT → PAID (no backward transitions)

**Value Object: LineItem**
- `id` (UUID)
- `description` (String, required)
- `quantity` (BigDecimal, required, > 0)
- `unitPrice` (BigDecimal, required, >= 0)
- `total` (BigDecimal, calculated: quantity * unitPrice)

---

#### Payment Domain Entity
**Location**: `com.invoiceme.domain.payments.Payment`

**Properties**:
- `id` (UUID, primary key)
- `invoiceId` (UUID, required, foreign key)
- `amount` (BigDecimal, required, > 0)
- `paymentDate` (LocalDate, required)
- `paymentMethod` (String, optional: CASH, CREDIT_CARD, BANK_TRANSFER, etc.)
- `createdAt` (LocalDateTime)

**Domain Methods**:
- `validate()` - Validates amount > 0 and payment date is not in the future
- `validateAgainstInvoice(Invoice invoice)` - Validates payment doesn't exceed invoice balance

**Business Rules**:
- Payment amount must be positive
- Payment date cannot be in the future
- Payment cannot exceed invoice remaining balance

---

### Domain Events (Optional but Encouraged)

**Location**: `com.invoiceme.domain.events`

- `InvoiceCreatedEvent` - Published when invoice is created
- `InvoiceSentEvent` - Published when invoice is marked as sent
- `PaymentRecordedEvent` - Published when payment is recorded
- `InvoicePaidEvent` - Published when invoice balance reaches zero

**Implementation**: Use Spring's ApplicationEventPublisher or a custom event bus.

---

### Domain Exceptions

**Location**: `com.invoiceme.domain.exceptions`

- `InvalidInvoiceStateException` - Thrown when invalid state transition is attempted
- `InsufficientPaymentException` - Thrown when payment exceeds invoice balance
- `InvalidLineItemException` - Thrown when line item validation fails
- `DomainValidationException` - Generic domain validation exception

---

### Success Criteria

- ✅ All domain entities are pure Java objects (no framework dependencies)
- ✅ Business logic is encapsulated within domain entities
- ✅ Unit tests for domain logic pass (100% coverage for domain methods)
- ✅ Domain exceptions are properly defined
- ✅ Value objects are immutable

---

## Phase 3: CQRS Commands Implementation (Write Operations)

**Duration**: 3-4 days  
**Goal**: Implement all write operations following CQRS and Vertical Slice Architecture

### Architecture Pattern

Each command follows this structure:
```
application/
  └── {domain}/
      └── {command}/
          ├── {Command}.java          # Command DTO
          ├── {Command}Handler.java   # Command handler
          └── {Command}Validator.java # Optional validation
```

### Customer Commands

#### CreateCustomerCommand
**Location**: `com.invoiceme.application.customers.create`

- **Command**: `CreateCustomerCommand(name, email, address)`
- **Handler**: `CreateCustomerHandler`
  - Validates command
  - Creates Customer domain entity
  - Saves via CustomerRepository
  - Returns CustomerId

#### UpdateCustomerCommand
**Location**: `com.invoiceme.application.customers.update`

- **Command**: `UpdateCustomerCommand(customerId, name, email, address)`
- **Handler**: `UpdateCustomerHandler`
  - Loads Customer by ID
  - Calls domain method `updateDetails()`
  - Saves via CustomerRepository

#### DeleteCustomerCommand
**Location**: `com.invoiceme.application.customers.delete`

- **Command**: `DeleteCustomerCommand(customerId)`
- **Handler**: `DeleteCustomerHandler`
  - Validates customer exists
  - Checks if customer has invoices (business rule: cannot delete if has invoices)
  - Deletes via CustomerRepository

---

### Invoice Commands

#### CreateInvoiceCommand
**Location**: `com.invoiceme.application.invoices.create`

- **Command**: `CreateInvoiceCommand(customerId, issueDate, dueDate)`
- **Handler**: `CreateInvoiceHandler`
  - Validates customer exists
  - Creates Invoice in DRAFT status
  - Saves via InvoiceRepository
  - Publishes InvoiceCreatedEvent (optional)

#### UpdateInvoiceCommand
**Location**: `com.invoiceme.application.invoices.update`

- **Command**: `UpdateInvoiceCommand(invoiceId, issueDate, dueDate)`
- **Handler**: `UpdateInvoiceHandler`
  - Loads Invoice by ID
  - Validates status is DRAFT
  - Updates invoice details
  - Saves via InvoiceRepository

#### MarkInvoiceAsSentCommand
**Location**: `com.invoiceme.application.invoices.markAsSent`

- **Command**: `MarkInvoiceAsSentCommand(invoiceId)`
- **Handler**: `MarkInvoiceAsSentHandler`
  - Loads Invoice by ID
  - Calls domain method `markAsSent()`
  - Validates invoice has line items
  - Saves via InvoiceRepository
  - Publishes InvoiceSentEvent (optional)

#### AddLineItemCommand
**Location**: `com.invoiceme.application.invoices.addLineItem`

- **Command**: `AddLineItemCommand(invoiceId, description, quantity, unitPrice)`
- **Handler**: `AddLineItemHandler`
  - Loads Invoice by ID
  - Validates status is DRAFT
  - Creates LineItem value object
  - Calls domain method `addLineItem()`
  - Saves via InvoiceRepository

#### RemoveLineItemCommand
**Location**: `com.invoiceme.application.invoices.removeLineItem`

- **Command**: `RemoveLineItemCommand(invoiceId, lineItemId)`
- **Handler**: `RemoveLineItemHandler`
  - Loads Invoice by ID
  - Validates status is DRAFT
  - Calls domain method `removeLineItem()`
  - Saves via InvoiceRepository

---

### Payment Commands

#### RecordPaymentCommand
**Location**: `com.invoiceme.application.payments.record`

- **Command**: `RecordPaymentCommand(invoiceId, amount, paymentDate, paymentMethod)`
- **Handler**: `RecordPaymentHandler`
  - Loads Invoice by ID
  - Validates invoice status is SENT or PAID
  - Creates Payment domain entity
  - Validates payment amount doesn't exceed balance
  - Calls domain method `applyPayment()` on Invoice
  - Saves Payment via PaymentRepository
  - Updates Invoice via InvoiceRepository
  - Publishes PaymentRecordedEvent and InvoicePaidEvent (if applicable)

---

### Repository Interfaces

**Location**: `com.invoiceme.domain.{domain}.{Domain}Repository` (interfaces in domain layer)

- `CustomerRepository` - Interface in domain, implementation in infrastructure
- `InvoiceRepository` - Interface in domain, implementation in infrastructure
- `PaymentRepository` - Interface in domain, implementation in infrastructure

**Implementation**: Use Spring Data JPA repositories in infrastructure layer.

---

### Transaction Management

- All command handlers should be transactional (`@Transactional`)
- Use Spring's declarative transaction management
- Handle optimistic locking for concurrent updates

---

### Success Criteria

- ✅ All commands execute successfully
- ✅ Invoice balance calculation is correct
- ✅ State transitions (DRAFT → SENT → PAID) are enforced
- ✅ Integration tests for command handlers pass
- ✅ Domain validation is properly enforced
- ✅ Transactions are properly managed

---

## Phase 4: CQRS Queries Implementation (Read Operations)

**Duration**: 2-3 days  
**Goal**: Implement all read operations with optimized query models

### Architecture Pattern

Each query follows this structure:
```
application/
  └── {domain}/
      └── {query}/
          ├── {Query}.java          # Query DTO
          ├── {Query}Handler.java   # Query handler
          └── {Query}Result.java    # Query result DTO
```

### Customer Queries

#### GetCustomerByIdQuery
**Location**: `com.invoiceme.application.customers.getById`

- **Query**: `GetCustomerByIdQuery(customerId)`
- **Result**: `CustomerDto(id, name, email, address, createdAt, updatedAt)`
- **Handler**: `GetCustomerByIdHandler`
  - Loads Customer by ID
  - Maps to CustomerDto
  - Returns CustomerDto or throws NotFoundException

#### ListAllCustomersQuery
**Location**: `com.invoiceme.application.customers.listAll`

- **Query**: `ListAllCustomersQuery(page, size, sortBy)`
- **Result**: `PagedResult<CustomerDto>`
- **Handler**: `ListAllCustomersHandler`
  - Loads customers with pagination
  - Maps to CustomerDto list
  - Returns paginated result

---

### Invoice Queries

#### GetInvoiceByIdQuery
**Location**: `com.invoiceme.application.invoices.getById`

- **Query**: `GetInvoiceByIdQuery(invoiceId)`
- **Result**: `InvoiceDto(id, customerId, customerName, status, issueDate, dueDate, totalAmount, balance, lineItems, payments, createdAt, updatedAt)`
- **Handler**: `GetInvoiceByIdHandler`
  - Loads Invoice with line items and payments (eager loading or separate queries)
  - Loads Customer for customer name
  - Maps to InvoiceDto
  - Returns InvoiceDto

#### ListInvoicesByStatusQuery
**Location**: `com.invoiceme.application.invoices.listByStatus`

- **Query**: `ListInvoicesByStatusQuery(status, page, size)`
- **Result**: `PagedResult<InvoiceSummaryDto>`
- **Handler**: `ListInvoicesByStatusHandler`
  - Loads invoices filtered by status with pagination
  - Maps to InvoiceSummaryDto (lightweight DTO)
  - Returns paginated result

#### ListInvoicesByCustomerQuery
**Location**: `com.invoiceme.application.invoices.listByCustomer`

- **Query**: `ListInvoicesByCustomerQuery(customerId, page, size)`
- **Result**: `PagedResult<InvoiceSummaryDto>`
- **Handler**: `ListInvoicesByCustomerHandler`
  - Loads invoices filtered by customer with pagination
  - Maps to InvoiceSummaryDto
  - Returns paginated result

---

### Payment Queries

#### GetPaymentByIdQuery
**Location**: `com.invoiceme.application.payments.getById`

- **Query**: `GetPaymentByIdQuery(paymentId)`
- **Result**: `PaymentDto(id, invoiceId, invoiceNumber, amount, paymentDate, paymentMethod, createdAt)`
- **Handler**: `GetPaymentByIdHandler`
  - Loads Payment by ID
  - Loads Invoice for invoice number
  - Maps to PaymentDto
  - Returns PaymentDto

#### ListPaymentsByInvoiceQuery
**Location**: `com.invoiceme.application.payments.listByInvoice`

- **Query**: `ListPaymentsByInvoiceQuery(invoiceId)`
- **Result**: `List<PaymentDto>`
- **Handler**: `ListPaymentsByInvoiceHandler`
  - Loads all payments for invoice
  - Maps to PaymentDto list
  - Returns list

---

### Query Optimization

- Use Spring Data JPA projections for read-only queries
- Implement custom queries for complex joins
- Use database indexes (see PRD-04-Infrastructure.md)
- Consider read models/views for complex queries (optional)

---

### Success Criteria

- ✅ All queries return correct data
- ✅ Query response times < 200ms (local environment)
- ✅ Integration tests for query handlers pass
- ✅ Pagination works correctly
- ✅ DTOs are properly mapped from domain entities

---

## Phase 5: REST API Layer & Integration

**Duration**: 2-3 days  
**Goal**: Expose CQRS operations via RESTful APIs

### API Structure

**Base Path**: `/api/v1`

**Controllers Location**: `com.invoiceme.api.{domain}.{Domain}Controller`

### Customer API Endpoints

**Controller**: `CustomerController`

- `POST /api/v1/customers` - CreateCustomerCommand
  - Request: `CreateCustomerRequest(name, email, address)`
  - Response: `CustomerResponse(id, name, email, address, createdAt)`
  - Status: 201 Created

- `PUT /api/v1/customers/{id}` - UpdateCustomerCommand
  - Request: `UpdateCustomerRequest(name, email, address)`
  - Response: `CustomerResponse`
  - Status: 200 OK

- `DELETE /api/v1/customers/{id}` - DeleteCustomerCommand
  - Response: 204 No Content
  - Status: 204 No Content

- `GET /api/v1/customers/{id}` - GetCustomerByIdQuery
  - Response: `CustomerResponse`
  - Status: 200 OK

- `GET /api/v1/customers` - ListAllCustomersQuery
  - Query Params: `page`, `size`, `sortBy`
  - Response: `PagedResponse<CustomerResponse>`
  - Status: 200 OK

---

### Invoice API Endpoints

**Controller**: `InvoiceController`

- `POST /api/v1/invoices` - CreateInvoiceCommand
  - Request: `CreateInvoiceRequest(customerId, issueDate, dueDate)`
  - Response: `InvoiceResponse`
  - Status: 201 Created

- `PUT /api/v1/invoices/{id}` - UpdateInvoiceCommand
  - Request: `UpdateInvoiceRequest(issueDate, dueDate)`
  - Response: `InvoiceResponse`
  - Status: 200 OK

- `POST /api/v1/invoices/{id}/send` - MarkInvoiceAsSentCommand
  - Response: `InvoiceResponse`
  - Status: 200 OK

- `POST /api/v1/invoices/{id}/line-items` - AddLineItemCommand
  - Request: `AddLineItemRequest(description, quantity, unitPrice)`
  - Response: `InvoiceResponse`
  - Status: 200 OK

- `DELETE /api/v1/invoices/{id}/line-items/{lineItemId}` - RemoveLineItemCommand
  - Response: `InvoiceResponse`
  - Status: 200 OK

- `GET /api/v1/invoices/{id}` - GetInvoiceByIdQuery
  - Response: `InvoiceResponse`
  - Status: 200 OK

- `GET /api/v1/invoices?status={status}` - ListInvoicesByStatusQuery
  - Query Params: `status`, `page`, `size`
  - Response: `PagedResponse<InvoiceSummaryResponse>`
  - Status: 200 OK

- `GET /api/v1/invoices?customerId={customerId}` - ListInvoicesByCustomerQuery
  - Query Params: `customerId`, `page`, `size`
  - Response: `PagedResponse<InvoiceSummaryResponse>`
  - Status: 200 OK

---

### Payment API Endpoints

**Controller**: `PaymentController`

- `POST /api/v1/payments` - RecordPaymentCommand
  - Request: `RecordPaymentRequest(invoiceId, amount, paymentDate, paymentMethod)`
  - Response: `PaymentResponse`
  - Status: 201 Created

- `GET /api/v1/payments/{id}` - GetPaymentByIdQuery
  - Response: `PaymentResponse`
  - Status: 200 OK

- `GET /api/v1/invoices/{invoiceId}/payments` - ListPaymentsByInvoiceQuery
  - Response: `List<PaymentResponse>`
  - Status: 200 OK

---

### API Implementation Details

#### Request/Response DTOs
- Create separate DTOs for API layer (different from application layer DTOs)
- Use Jakarta Bean Validation annotations (`@NotNull`, `@Email`, `@Min`, etc.)
- Map between API DTOs and Command/Query DTOs using mappers (MapStruct recommended)

#### Exception Handling
**Location**: `com.invoiceme.api.exceptions.GlobalExceptionHandler`

- `@ControllerAdvice` for global exception handling
- Map domain exceptions to HTTP status codes:
  - `NotFoundException` → 404 Not Found
  - `InvalidInvoiceStateException` → 400 Bad Request
  - `InsufficientPaymentException` → 400 Bad Request
  - `DomainValidationException` → 400 Bad Request
  - `IllegalArgumentException` → 400 Bad Request
  - Generic exceptions → 500 Internal Server Error

#### CORS Configuration
- Configure CORS for frontend origin
- Allow necessary HTTP methods and headers

#### API Documentation
- Use SpringDoc OpenAPI to generate Swagger/OpenAPI documentation
- Add `@Operation` and `@ApiResponse` annotations
- Accessible at `/swagger-ui.html` or `/api-docs`

#### API Versioning
- Use URL path versioning: `/api/v1/...`
- Prepare for future versions

---

### Success Criteria

- ✅ All endpoints return correct HTTP status codes
- ✅ Request validation works (400 for invalid input)
- ✅ API documentation is accessible via Swagger UI
- ✅ Integration tests for all endpoints pass
- ✅ CORS is properly configured
- ✅ Exception handling returns user-friendly error messages

---

## Phase 8: Authentication & Authorization (Backend)

**Duration**: 1-2 days (backend portion)  
**Goal**: Secure the application with basic authentication

### User Domain Entity

**Location**: `com.invoiceme.domain.users.User`

**Properties**:
- `id` (UUID, primary key)
- `username` (String, unique, required)
- `email` (String, unique, required)
- `passwordHash` (String, required) - BCrypt hashed
- `createdAt` (LocalDateTime)

**Domain Methods**:
- `validatePassword(String plainPassword)` - Validates password against hash
- `changePassword(String newPlainPassword)` - Updates password hash

---

### Authentication Commands

#### RegisterUserCommand (Optional)
**Location**: `com.invoiceme.application.auth.register`

- **Command**: `RegisterUserCommand(username, email, password)`
- **Handler**: `RegisterUserHandler`
  - Validates username and email uniqueness
  - Hashes password with BCrypt
  - Creates User entity
  - Saves via UserRepository

#### LoginCommand
**Location**: `com.invoiceme.application.auth.login`

- **Command**: `LoginCommand(username, password)`
- **Handler**: `LoginHandler`
  - Loads User by username
  - Validates password
  - Generates JWT token
  - Returns JwtTokenResponse

---

### Spring Security Configuration

**Location**: `com.invoiceme.infrastructure.security.SecurityConfig`

- Configure JWT authentication
- Secure all `/api/**` endpoints (require authentication)
- Allow public access to `/api/auth/login` and `/api/auth/register`
- Configure password encoder (BCrypt)
- Configure JWT token generation and validation

### JWT Implementation

- Use `jjwt` library for JWT token generation/validation
- Token should include: username, roles (if applicable), expiration
- Token expiration: 24 hours (configurable)
- Store secret key in environment variables

---

### Success Criteria

- ✅ All API endpoints (except auth) require authentication
- ✅ JWT tokens are properly generated and validated
- ✅ Password hashing uses BCrypt
- ✅ Unauthenticated requests return 401 Unauthorized
- ✅ Integration tests for authentication pass

---

## Database Schema

See **PRD-04-Infrastructure.md** for complete schema. Core tables:

- `customers` - Customer information
- `invoices` - Invoice headers
- `invoice_line_items` - Invoice line items
- `payments` - Payment records
- `users` - Authentication users

---

## Testing Requirements

See **PRD-03-Testing.md** for detailed testing requirements. Backend testing includes:

- Unit tests for domain entities
- Unit tests for command/query handlers
- Integration tests for repositories
- Integration tests for API endpoints
- Integration tests for authentication

---

**Document Version**: 1.0  
**Last Updated**: [Current Date]  
**Author**: Development Team


